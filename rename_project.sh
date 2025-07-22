#!/bin/bash

# 오류 시 중지
set -e

# 🛠️ 입력값
OLD_NAME="usermanagement"                  # 원본 프로젝트 디렉토리명
NEW_NAME="userbatch"                    # 새 프로젝트 디렉토리명
PACKAGE_PREFIX="io.github.hachanghyun"     # 유지할 패키지 prefix
OLD_PACKAGE="$PACKAGE_PREFIX.$OLD_NAME"
NEW_PACKAGE="$PACKAGE_PREFIX.$NEW_NAME"

echo "🔄 기존 프로젝트 '$OLD_NAME'을 '$NEW_NAME'으로 복사 중..."
cp -r "$OLD_NAME" "$NEW_NAME"

cd "$NEW_NAME"

# 💬 내부 문자열 치환 (파일 내의 프로젝트명, 패키지명 등)
echo "🔍 내부 문자열 치환 중 (패키지명, 프로젝트명 등)..."
find . -type f \( -name "*.java" -o -name "*.kt" -o -name "*.gradle" -o -name "*.xml" -o -name "*.properties" \) \
  -exec sed -i "" "s/$OLD_PACKAGE/$NEW_PACKAGE/g" {} +
find . -type f \( -name "*.java" -o -name "*.kt" -o -name "*.gradle" -o -name "*.xml" -o -name "*.properties" \) \
  -exec sed -i "" "s/$OLD_NAME/$NEW_NAME/g" {} +

# 📦 패키지 디렉토리 구조 변경
echo "📦 패키지 디렉토리 구조 변경 중..."
OLD_PACKAGE_PATH="src/main/java/$(echo $OLD_PACKAGE | tr '.' '/')"
NEW_PACKAGE_PATH="src/main/java/$(echo $NEW_PACKAGE | tr '.' '/')"

mkdir -p "$NEW_PACKAGE_PATH"
cp -r "$OLD_PACKAGE_PATH/"* "$NEW_PACKAGE_PATH/"
rm -rf "$OLD_PACKAGE_PATH"

# 🔧 settings.gradle 수정
echo "🔧 settings.gradle 수정 중..."
sed -i "" "s/rootProject.name = '$OLD_NAME'/rootProject.name = '$NEW_NAME'/" settings.gradle

echo "✅ 프로젝트 복제 및 패키지명 변경 완료: $NEW_NAME"
