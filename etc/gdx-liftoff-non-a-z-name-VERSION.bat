@echo OFF
(IF NOT EXIST "%ProgramData%/gdx-liftoff/temp" MKDIR "%ProgramData%/gdx-liftoff/temp") && SET TMP=temp && copy gdx-liftoff-VERSION.jar "%ProgramData%/gdx-liftoff\gdx-liftoff-VERSION.jar" && (START /D "%ProgramData%/gdx-liftoff" javaw -jar gdx-liftoff-VERSION.jar)
