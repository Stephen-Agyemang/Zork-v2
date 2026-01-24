#!/bin/bash
# Direct javac build script - bypasses Maven compiler bug
# This script compiles Zork v2 correctly when Maven fails

cd "$(dirname "$0")"

JAVA_HOME="/Library/Java/JavaVirtualMachines/temurin-21.jdk/Contents/Home"
JAVAC="$JAVA_HOME/bin/javac"

SRC_DIR="src/main/java"
TARGET_DIR="target/classes"
GSON_JAR="$HOME/.m2/repository/com/google/code/gson/gson/2.10.1/gson-2.10.1.jar"

# Clean
rm -rf "$TARGET_DIR"
mkdir -p "$TARGET_DIR"

echo "🔨 Compiling Zork v2 with javac (Java 21)..."
echo "Source: $SRC_DIR"
echo "Output: $TARGET_DIR"

# Compile all Java files in correct dependency order
"$JAVAC" -d "$TARGET_DIR" -cp "$GSON_JAR" \
  "$SRC_DIR/com/mygroup/Item.java" \
  "$SRC_DIR/com/mygroup/Location.java" \
  "$SRC_DIR/com/mygroup/ContainerItem.java" \
  "$SRC_DIR/com/mygroup/RandomWordService.java" \
  "$SRC_DIR/com/mygroup/GameState.java" \
  "$SRC_DIR/com/mygroup/GameEngine.java" \
  "$SRC_DIR/com/mygroup/App.java" \
  "$SRC_DIR/com/mygroup/CommandRouter.java" \
  "$SRC_DIR/com/mygroup/WorldBuilder.java" \
  "$SRC_DIR/com/mygroup/Main.java" \
  "$SRC_DIR/com/mygroup/TestMain.java"

if [ $? -eq 0 ]; then
  echo "✅ Compilation successful!"
  echo "📦 Classes built in: $TARGET_DIR"
  echo ""
  echo "📄 Compiled classes:"
  find "$TARGET_DIR" -name "*.class" | sed 's|'$TARGET_DIR'/||' | sort
  
  echo ""
  echo "🎮 To run the game:"
  echo "   java -cp target/classes com.mygroup.Main"
  
else
  echo "❌ Compilation failed!"
  exit 1
