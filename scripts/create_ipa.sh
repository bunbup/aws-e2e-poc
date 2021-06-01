#!/bin/bash
set -eo pipefail -o xtrace

PROJECT=swiftSdkTest
CURRENT_DIR=`pwd`
TMPDIR=`mktemp -d /tmp/${PROJECT}.XXX` || exit 1
BUILD_PATH=$TMPDIR/$PROJECT/build
PAYLOAD_PATH=$TMPDIR/Payload
XCTEST_DIR_NAME=${PROJECT}Tests.xctest
XCTEST_ZIP_PATH=$TMPDIR/$XCTEST_DIR_NAME.zip

pod install
xcodebuild build-for-testing -workspace ./$PROJECT.xcworkspace -allowProvisioningUpdates -scheme ${PROJECT} -derivedDataPath $BUILD_PATH -verbose
mkdir $PAYLOAD_PATH
cp -r $BUILD_PATH/Build/Products/Debug-iphoneos/$PROJECT.app $PAYLOAD_PATH
cd $PAYLOAD_PATH && zip -r -X "../Payload.ipa" . && cd -
cp -r $BUILD_PATH/Build/Products/Debug-iphoneos/$PROJECT.app/PlugIns/$XCTEST_DIR_NAME $TMPDIR
cd $TMPDIR/$XCTEST_DIR_NAME && zip -r -X "$XCTEST_ZIP_PATH" . && cd - 
cd $CURRENT_DIR 
mv $TMPDIR/Payload.ipa  .
mv $XCTEST_ZIP_PATH . 