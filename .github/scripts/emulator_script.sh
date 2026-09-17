echo "Installing APK..."
adb install app/build/outputs/apk/release/app-release.apk

echo "Clearing old system logs..."
adb logcat -c

echo "Launching main activity..."
adb shell monkey -p ru.artyommukhin.lvtrainer -c android.intent.category.LAUNCHER 1

echo "Waiting 10 seconds to verify app stability..."
sleep 10

echo "Checking if app process is still alive..."
if adb shell pidof ru.artyommukhin.lvtrainer > /dev/null; then
  echo "✅ Success: App is running successfully!"
  exit 0
else
  echo "❌ Error: App crashed or failed to start!"
  echo "Dumping full logcat to logcat.txt..."
  adb logcat -d > logcat.txt
  exit 1
fi