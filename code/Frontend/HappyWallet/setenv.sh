#!/bin/bash
export ANDROID_HOME="$(pwd)/cmdline-tools"
export PATH=$PATH:$ANDROID_HOME/latest/bin:$ANDROID_HOME/platform-tools:$ANDROID_HOME/emulator
alias sdkmanager="cmd.exe /c $(pwd)/cmdline-tools/latest/bin/sdkmanager.bat"
