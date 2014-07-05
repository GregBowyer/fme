# FermaT Maintenance Environment (FME)

The FermaT Maintenance Environment is an interactive front-end to the [FermaT Program Transformation System](http://www.gkc.org.uk/fermat.html).

## Author

The FME was written by [Matthias Ladkau](http://www.ladkau.de).

## License

The FME is released under the [GPL](http://www.gnu.org/licenses/gpl-3.0).

## How to build the FME

* In the moment the build works only on Windows. You need to have Java 7 or later and Apache Ant. Both commands should be in your PATH.

* Go to `Workspace/Fme` and run `ant assemble` this should build a distribution version in the `Build` directory.

## How to use the FME with Eclipse

* Create a new Workspace and point it to the `Workspace` directory in the checkout.

* Create a new `Fme` project and make sure to put all libraries in the `lib` directory in the CLASSPATH for the project.

* Run the `env.bat` in the `Workspace` directory at least once to make sure all dependecies get extracted - this needs to be done only once.

## Using the FME

The FME requires a working Java 7 (or later) environment. There is a short tutorial document in the `Misc/doc` folder - it needs updating but it should still be useful. The first start of the FME will take a bit longer because several libraries and packages need to be extracted.

## TODO

* Documentation needs expanding / updating.
