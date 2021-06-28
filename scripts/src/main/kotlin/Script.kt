@file:DependsOn("software.amazon.awssdk:devicefarm:2.16.49")
@file:DependsOn("com.squareup.okhttp3:okhttp:4.9.1")
@file:DependsOn("org.slf4j:slf4j-simple:1.7.30")
@file:CompilerOpts("-jvm-target 1.8")

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.devicefarm.DeviceFarmClient
import software.amazon.awssdk.services.devicefarm.model.*
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.time.Duration
import java.util.*
import kotlin.system.exitProcess

private val region = Region.US_WEST_2

//private val projectArn = "arn:aws:devicefarm:us-west-2:806583214236:project:689ad938-8bac-40a4-a511-da1afafb3c50"
//private val devicePoolArn = "arn:aws:devicefarm:us-west-2:806583214236:devicepool:689ad938-8bac-40a4-a511-da1afafb3c50/24745f40-31b0-4ce6-ad0b-4936af61e16f"
//"../app/build/outputs/apk/debug/app-debug.apk"
//"../app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk"
private val client = OkHttpClient()
private val uniqueName = "e2e-aws-poc-${UUID.randomUUID().toString().take(8)}"
private val logger: Logger = LoggerFactory.getLogger(Script::class.java)


fun wait(
    interval: Duration = Duration.ofSeconds(3),
    maxNumberOfIntervals: Int = 20,
    timeoutBlock: () -> Unit = {},
    block: () -> Boolean
) {
    var currentInterval = 0
    while (true) {
        if (block()) {
            return
        }
        currentInterval++
        if (currentInterval >= maxNumberOfIntervals) {
            timeoutBlock()
            logger.error("Timeout. Waited for $currentInterval calls in ${interval.toMillis()} millis intervals")
            exitProcess(5)
        }
        Thread.sleep(interval.toMillis())
    }
}


fun DeviceFarmClient.confirmUpload(arn: String) {
    wait {
        val resp = getUpload { it.arn(arn) }
        logger.info("Upload $arn status: ${resp.upload().status()}")
        when (resp.upload().status()) {
            UploadStatus.INITIALIZED, UploadStatus.PROCESSING -> false
            UploadStatus.SUCCEEDED -> true
            null, UploadStatus.FAILED, UploadStatus.UNKNOWN_TO_SDK_VERSION -> {
                logger.error("Upload $arn failed due to: ${resp.upload().message()}")
                exitProcess(2)
            }
        }

    }
}


fun DeviceFarmClient.waitForTests(arn: String) {
    wait(
        interval = Duration.ofSeconds(10),
        maxNumberOfIntervals = 150,
        timeoutBlock = { stopRun { it.arn(arn) } }
    ) {
        val resp = getRun { it.arn(arn) }
        logger.info("Tests $arn status: ${resp.run().status()}")
        when (resp.run().status()) {
            ExecutionStatus.COMPLETED -> when (resp.run().result()) {
                ExecutionResult.PASSED -> {
                    downloadArtifacts(arn)
                    true
                }
                ExecutionResult.FAILED, ExecutionResult.ERRORED -> {
                    downloadArtifacts(arn)
                    logger.error("Run $arn failed due to: ${resp.run().message()}")
                    exitProcess(7)
                }
                else -> {
                    downloadArtifacts(arn)
                    logger.error("Run $arn have unexpected ExecutionResult: ${resp.run().result()}")
                    exitProcess(8)
                }
            }
            null, ExecutionStatus.UNKNOWN_TO_SDK_VERSION -> {
                logger.error("Run $arn failed due to: ${resp.run().message()}")
                stopRun { it.arn(arn) }
                exitProcess(6)
            }
            else -> {
                false
            }
        }
    }
}

fun DeviceFarmClient.downloadArtifacts(arn: String) {
    val resp = listJobs { it.arn(arn) }
    val basePath = Files.createDirectories(Paths.get("./artifacts"))
    resp.jobs().forEach { job ->
        listSuites { it.arn(job.arn()) }.suites().forEach { suite ->
            listTests { it.arn(suite.arn()) }.tests().forEach { test ->
                listArtifacts { it.arn(test.arn()).type(ArtifactCategory.LOG) }.artifacts().forEach { artifact ->
                    client.newCall(
                        Request.Builder()
                            .get()
                            .url(artifact.url())
                            .build()
                    )
                        .execute().use {
                            if (it.code == 200) {
                                val filePath = Paths.get(
                                    basePath.toString(),
                                    "${job.name()}-${suite.name()}-${
                                        test.name().replace(":", "_")
                                    }-${artifact.type()}-${artifact.name()}.${artifact.extension()}"
                                )
                                logger.info("Writing response to $filePath")
                                Files.write(
                                    filePath,
                                    it.body!!.bytes(),
                                    StandardOpenOption.TRUNCATE_EXISTING,
                                    StandardOpenOption.WRITE,
                                    StandardOpenOption.CREATE
                                )
                            }
                        }
                }
            }
        }
    }
}


fun DeviceFarmClient.upload(name: String, path: String, type: UploadType, projectArn: String): String {
    logger.info("Creating upload $name")
    val createUpload = createUpload { b ->
        b.name(name)
            .type(type)
            .contentType("application/octet-stream")
            .projectArn(projectArn)
            .build()
    }
    val uploadArn = createUpload.upload().arn()
    val uploadUrl = createUpload.upload().url()

    logger.info("Upload $name created with arn: $uploadArn")
    logger.info("Uploading $name")
    client.newCall(
        Request.Builder()
            .put(File(path).asRequestBody())
            .addHeader("content-type", "application/octet-stream")
            .url(uploadUrl)
            .build()
    ).execute().use { uploadResponse ->
        if (uploadResponse.code != 200) {
            logger.error(
                "Upload failed ${uploadResponse.code} -> ${
                    uploadResponse.body?.bytes()?.decodeToString()
                }"
            )
            exitProcess(1)
        } else {
            logger.info("Upload succeeded")
        }
    }

    confirmUpload(uploadArn)

    return uploadArn
}

private fun DeviceFarmClient.runTests(
    appArn: String,
    testArn: String,
    projectArn: String,
    devicePoolArn: String,
    testType: TestType,
    customTestSpec: String?
): ScheduleRunResponse? {
    val result = scheduleRun {
        it.projectArn(projectArn)
            .devicePoolArn(devicePoolArn)
            .appArn(appArn)
            .name(uniqueName)
            .test { t ->
                (customTestSpec?.let { testSpec -> t.testSpecArn(testSpec) } ?: t)
                    .testPackageArn(testArn)
                    .type(testType)
                    .build()
            }.build()
    }
    waitForTests(result.run().arn())
    return result
}

object Script

data class TypesAndNames(
    val appType: UploadType,
    val appName: String,
    val testsType: UploadType,
    val testsName: String,
    val testType: TestType
)

fun main(args: Array<String>) {
    val map: Map<String, String> = args.fold(Pair(emptyMap<String, String>(), "")) { (map, lastKey), elem ->
        if (elem.startsWith("-")) Pair(map, elem)
        else Pair(map + (lastKey to elem), "")
    }.first

    println(map)

    check(map.keys.containsAll(listOf("--appPath", "--testsPath", "--projectArn", "--devicePoolArn")))

    val appPath: String = map["--appPath"]!!
    val testsPath: String = map["--testsPath"]!!
    val projectArn: String = map["--projectArn"]!!
    val devicePoolArn: String = map["--devicePoolArn"]!!

    val (appType, appName, testsType, testsName, testType) = if (appPath.endsWith("apk")) {
        TypesAndNames(UploadType.ANDROID_APP,
        "app-$uniqueName.apk",
        UploadType.INSTRUMENTATION_TEST_PACKAGE,
        "tests-$uniqueName.apk",
        TestType.INSTRUMENTATION)
    } else {
        TypesAndNames(UploadType.IOS_APP,
        "app-$uniqueName.ipa",
        UploadType.XCTEST_TEST_PACKAGE,
        "tests-$uniqueName.xctest.zip",
        TestType.XCTEST)
    }

    val deviceFarmClient = DeviceFarmClient.builder()
        .region(region)
        .build()
    val appArn = deviceFarmClient.upload(
        name = appName,
        path = appPath,
        type = appType,
        projectArn = projectArn
    )

    val testArn = deviceFarmClient.upload(
        name = testsName,
        path = testsPath,
        type = testsType,
        projectArn = projectArn
    )

    deviceFarmClient.runTests(
        appArn = appArn,
        testArn = testArn,
        projectArn = projectArn,
        devicePoolArn = devicePoolArn,
        testType = testType,
        customTestSpec = if (appPath.endsWith("apk")) "arn:aws:devicefarm:us-west-2:806583214236:upload:689ad938-8bac-40a4-a511-da1afafb3c50/67e12060-b99a-4bc8-b1d7-df88568c94e2" else null
    )
}
