//
//  TestInObjCTest.m
//  TestInObjCTest
//
//  Created by Lukasz Klich on 02/06/2021.
//

#import <XCTest/XCTest.h>

@interface TestInObjCTest : XCTestCase

@end

@implementation TestInObjCTest

- (void)setUp {
    // Put setup code here. This method is called before the invocation of each test method in the class.
}

- (void)tearDown {
    // Put teardown code here. This method is called after the invocation of each test method in the class.
}

- (void)testAnotherExample {
    // This is an example of a functional test case.
    // Use XCTAssert and related functions to verify your tests produce the correct results.
    XCTAssert(YES, @"Pass");
}

@end
