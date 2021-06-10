//
//  swiftSdkTest3Tests.m
//  swiftSdkTest3Tests
//
//  Created by Lukasz Klich on 08/06/2021.
//  Copyright © 2021 Lukasz Klich. All rights reserved.
//

#import <XCTest/XCTest.h>
#import <PubNub/PubNub.h>


@interface swiftSdkTest3Tests : XCTestCase

@end

@implementation swiftSdkTest3Tests

- (void)setUp {
    // Put setup code here. This method is called before the invocation of each test method in the class.
}

- (void)tearDown {
    // Put teardown code here. This method is called after the invocation of each test method in the class.
}

- (void)waitToCompleteIn:(NSTimeInterval)delay
               codeBlock:(void(^)(dispatch_block_t handler))codeBlock {
    
    dispatch_semaphore_t semaphore = dispatch_semaphore_create(0);
    __block BOOL handlerCalled = NO;
    
    codeBlock(^{
        if (!handlerCalled) {
            handlerCalled = YES;
            dispatch_semaphore_signal(semaphore);
        }
    });
    
    dispatch_time_t timeout = dispatch_time(DISPATCH_TIME_NOW, (int64_t)(delay * NSEC_PER_SEC));
    dispatch_semaphore_wait(semaphore, timeout);
    
    XCTAssertTrue(handlerCalled);
}


- (void)testExample {
    // This is an example of a functional test case.
    // Use XCTAssert and related functions to verify your tests produce the correct results
    NSString *pubKey = [[NSBundle mainBundle] objectForInfoDictionaryKey:@"PUBNUB_PUB_KEY"];
    NSString *subKey = [[NSBundle mainBundle] objectForInfoDictionaryKey:@"PUBNUB_SUB_KEY"];
    XCTAssertTrue([[[NSBundle mainBundle] objectForInfoDictionaryKey:@"PUBNUB_PUB_KEY"] length] != 0);
    XCTAssertTrue([[[NSBundle mainBundle] objectForInfoDictionaryKey:@"PUBNUB_SUB_KEY"] length] != 0);
    PNConfiguration *pnconfig = [PNConfiguration configurationWithPublishKey:pubKey
                                                            subscribeKey:subKey];
    dispatch_queue_t callbackQueue = dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0);
    PubNub *client = [PubNub clientWithConfiguration:pnconfig callbackQueue:callbackQueue];
    client.logger.enabled = YES;
    [client.logger setLogLevel:PNVerboseLogLevel];
    NSTimeInterval testCompletionDelay = 20.0;
    
    [self waitToCompleteIn:testCompletionDelay codeBlock:^(dispatch_block_t handler) {
        [client publish: @{ @"entry": @"entry" } toChannel:@"test-channel"
              withCompletion:^(PNPublishStatus *status) {

            XCTAssertFalse(status.isError);
            XCTAssertNotNil(status.data.timetoken);
            XCTAssertEqual(status.statusCode, 200);
            
            handler();
        }];
    }];
}

- (void)testPerformanceExample {
    // This is an example of a performance test case.
    [self measureBlock:^{
        // Put the code you want to measure the time of here.
    }];
}

@end
