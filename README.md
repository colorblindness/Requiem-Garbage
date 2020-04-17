<h1 align="center">Requiem</h1>
<div align="center">
  <strong> 1.12.2 Forge minecraft cheat.</strong>
</div>
<br />

# How to setup
Clone the workspace
```
https://github.com/oHare2/Requiem.git
```
Go into folder and open up a command prompt and do

**Eclipse** -
gradlew setupDevWorkspace eclipse build

**Intelij** -
gradlew setupDevWorkspace idea genIntellijRuns build

# Open in IDE
**Eclipse**
```Right click -> New -> Java Project -> Browse location -> Select IngrosWare folder -> Finish```

**Intelij**
```Open -> Select Reqiuem folder -> Import gradle project```

# Run

Add ```-Dfml.coreMods.load=group.skids.requiem.mixin.launch.RequiemLoader``` to VM options.
