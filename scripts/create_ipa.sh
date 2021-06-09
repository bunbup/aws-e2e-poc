#!/bin/bash
set -eo pipefail -o xtrace

PROJECT=swiftSdkTest3
CURRENT_DIR=`pwd`
TMPDIR=`mktemp -d /tmp/${PROJECT}.XXX` || exit 1
BUILD_PATH=$TMPDIR/$PROJECT/build
PAYLOAD_PATH=$TMPDIR/Payload
XCTEST_DIR_NAME=${PROJECT}Tests.xctest
XCTEST_ZIP_PATH=$TMPDIR/$XCTEST_DIR_NAME.zip

#pod install
xcodebuild build-for-testing -project ./$PROJECT.xcodeproj -allowProvisioningUpdates -scheme ${PROJECT} -derivedDataPath $BUILD_PATH -verbose
mkdir -p $PAYLOAD_PATH/Payload
cp -r $BUILD_PATH/Build/Products/Debug-iphoneos/$PROJECT.app $PAYLOAD_PATH/Payload
cd $PAYLOAD_PATH && zip -r -X "../Payload.ipa" . && cd -
mkdir -p $TMPDIR/$XCTEST_DIR_NAME
cp -r $BUILD_PATH/Build/Products/Debug-iphoneos/$PROJECT.app/PlugIns/$XCTEST_DIR_NAME $TMPDIR/$XCTEST_DIR_NAME
cd $TMPDIR/$XCTEST_DIR_NAME && zip -r -X "$XCTEST_ZIP_PATH" . && cd - 
cd $CURRENT_DIR 
mv $TMPDIR/Payload.ipa  .
mv $XCTEST_ZIP_PATH .
