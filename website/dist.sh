sbt server/dist
cd client
npm run build
cd ..
unzip server/target/universal/server-0.5.zip
rm -rf dist
mv server-0.5 dist
cp -r server/public dist/public
cp -r client/dist dist/public/dist
chmod +x dist/bin/server
