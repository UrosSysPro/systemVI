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
From the root of the project
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
From the root of the project
`gradle :examples:compile-release`

### Run Voxels
```
gradle :voxel-world:run
```

## Screenshots
### Box2d
![box2d](docs/screenshots/box2d.png)
### Ray Marching (cpu)
![rayMarchingCpu](docs/screenshots/ray-marching-cpu.png)
### Ray Marching (compute shader)
![rayMarchingGpu](docs/screenshots/ray-marching-gpu.png)
### Model Importer 
![modelImporter](docs/screenshots/model-importer.png)
### Maze
![maze](docs/screenshots/maze.png)
#### Mandelbrot Set (fragment shader)
![mandelbrot](docs/screenshots/mandelbrot-set.png)
## Julia Set (fragment shader)
![julia](docs/screenshots/jullia-set.png)
## Fluid Simulation (cpu)
![fluid-cpu](docs/screenshots/fluid-cpu.png)
## Fluid Simulation (gpu)
![fluid-gpu](docs/screenshots/fluid-gpu.png)
## Fabrik (forward and backward reaching inverse kinematics)
![fabrik](docs/screenshots/fabrik.png)
## Atari Break Out
![breakout](docs/screenshots/breakout.png)
## Reaction Diffusion 
![reaction-diffusion](docs/screenshots/reaction-diffusion.png)
## Phong Shading With Normal Mapping
![normal-mapping](docs/screenshots/normal-mapping.png)
## Phong Shading With Normal Mapping
![normal-mapping](docs/screenshots/normal-mapping.png)

