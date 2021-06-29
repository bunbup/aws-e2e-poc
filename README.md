# Device Farm POC

## Android

The test is in the `./app` directory. This contains the android project that produces the application `apk` and tests `apk` which are both being sent to device farm
using the script in `./scripts/src/main/kotlin/Script.kt`. There's not much complication in for android tests since everything is generated by few gradle commands. It can be seen
in the `./.github/workflows/build_android.yml`

## iOS
The test is in the `swiftSdkTest3`. Unfortunately it was my third attempt and bundle names have to be unique, that's why it's `3` at the end. During the poc it turned out the tests written in swift are not being recognized 
therefore I've changed the language to objective-c, and to keep it simple I also changed the sdk from swift SDK to Objective-C SDK. There's a little bit work involved in testing iOS applications on
device farm. The `.ipa` files need to be signed so I had to upload my own certificate that could be used for this. The process of generating certificate might differ in case of paid developer account (could be easier).
The automatic process of running this is in `./.github/workflows/build_ios.yml` with two additional scripts in `./scripts/` directory, namely: `decrypt_secrets.sh` which decrypts the certificates, and the `create_ipa.sh` which 
builds the project with tests. Once again the upload to device farm is handled by `./scripts/src/main/kotlin/Script.kt`.

### Secret files

To encrypt them:

```
gpg --symmetric --cipher-algo AES256 <file>
```

## scripts

Initially for scripting I decided to check kotlin script instead of bash. The `./scripts/src/main/kotlin/Script.kt` together with the `./scripts/build.gradle` is the only script I've created

##

