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
`example_name` can be name of any dir in examples:<br>
`gradle :examples:fluid:run`<br>
`gradle :examples:fabrik:run`<br>

### Run Website Locally
From the root of the project
```
cd website
npm install 
npm run dev
```
### Compile a Release
`gradle :examples:compile-release`

### Run Voxels
```
gradle :voxel-world:run
```