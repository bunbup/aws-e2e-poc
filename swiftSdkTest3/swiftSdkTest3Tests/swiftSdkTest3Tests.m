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

- (void)testExample {
    // This is an example of a functional test case.
    // Use XCTAssert and related functions to verify your tests produce the correct results
    XCTAssertTrue([[[NSBundle mainBundle] objectForInfoDictionaryKey:@"PUBNUB_PUB_KEY"] length] != 0);
    XCTAssertTrue([[[NSBundle mainBundle] objectForInfoDictionaryKey:@"PUBNUB_SUB_KEY"] length] != 0);
    PNConfiguration *pnconfig = [PNConfiguration configurationWithPublishKey:@"myPublishKey"
                                                            subscribeKey:@"mySubscribeKey"];
    pnconfig.uuid = @"ReplaceWithYourClientIdentifier";
    PubNub *client = [PubNub clientWithConfiguration:pnconfig];
}

- (void)testPerformanceExample {
    // This is an example of a performance test case.
    [self measureBlock:^{
        // Put the code you want to measure the time of here.
    }];
}

@end
