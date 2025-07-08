#!/bin/bash

# 오류 시 중지
set -e

# 🛠️ 입력값
OLD_NAME="oldproject"            # 원본 프로젝트 디렉토리명
NEW_NAME="newproject"            # 새 프로젝트 디렉토리명
OLD_PACKAGE="com.example.old"    # 기존 package명
NEW_PACKAGE="com.example.new"    # 새 package명

echo "🔄 기존 프로젝트 '$OLD_NAME'을 '$NEW_NAME'으로 복사 중..."
cp -r "$OLD_NAME" "$NEW_NAME"

cd "$NEW_NAME"

# 💬 프로젝트 내 문자열 치환
echo "🔍 내부 문자열 치환 중 (프로젝트명, 패키지명 등)..."
find . -type f \( -name "*.java" -o -name "*.kt" -o -name "*.gradle" -o -name "*.xml" -o -name "*.properties" \) -exec sed -i "" "s/$OLD_NAME/$NEW_NAME/g" {} +
find . -type f \( -name "*.java" -o -name "*.kt" -o -name "*.gradle" -o -name "*.xml" -o -name "*.properties" \) -exec sed -i "" "s/$OLD_PACKAGE/$NEW_PACKAGE/g" {} +

# 📦 패키지 디렉토리 구조 변경
echo "📦 디렉토리 구조 이동 중..."
OLD_PACKAGE_PATH="src/main/java/$(echo $OLD_PACKAGE | tr '.' '/')"
NEW_PACKAGE_PATH="src/main/java/$(echo $NEW_PACKAGE | tr '.' '/')"

mkdir -p "$NEW_PACKAGE_PATH"
cp -r "$OLD_PACKAGE_PATH/"* "$NEW_PACKAGE_PATH/"
rm -rf "$OLD_PACKAGE_PATH"

# 🔧 설정 파일 수정
echo "🔧 settings.gradle 수정 중..."
sed -i "" "s/rootProject.name = '$OLD_NAME'/rootProject.name = '$NEW_NAME'/" settings.gradle

echo "✅ 프로젝트 복제 및 이름 변경 완료: $NEW_NAME"
