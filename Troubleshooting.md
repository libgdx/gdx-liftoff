# Troubleshooting

This is organized as a list of possible messages or signs there is a problem,
followed by any ways that help resolve those problems.

This guide is brand new, and will be added to as new solutions are found for problems.

### When importing an Android project, an error mentions SeekableByteChannel.

Specifically, the error involves `void org.apache.commons.compress.archivers.zip.ZipFile.<init>(java.nio.channels.SeekableByteChannel)'`.
The solution for this is to install the Android Build Tools, version 33.0.2 . I don't know if newer versions work as
well, but you can have 33.0.2 installed in addition to other versions.

To install new build tools, go to Tools -> Android -> SDK Manager, SDK Tools tab, check "Show Package Details" at the
bottom, select Build Tools version 33.0.2, and click Apply or OK. Then follow the installation steps.
