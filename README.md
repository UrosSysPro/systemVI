# SystemVi
A project containing source code for:
 - systemvi game engine
 - games
 - mechanical keyboard firmware
 - website
 - server
 ## Download
```
git clone https://github.com/UrosSysPro/systemVI.git
cd systemVI 
gradle build
```
### Run Examples
```
gradle :examples:example_name:run
```
`example_name` can be name of any dir in examples

### Run Website Locally
From the root of the project
```
cd website
npm install 
npm run dev
```
### Compile a Release
`gradle compile-release`