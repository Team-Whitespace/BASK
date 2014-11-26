echo "Cloning and building Luwak"
git clone -b 1.1.x https://github.com/bloomberg/luwak.git
cd luwak
git reset --hard HEAD; git clean -f -d; git pull
mvn clean install
cd ..

echo "Building BASK"
mvn clean install
