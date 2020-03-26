package com.github.czyzby.lml.vis.parser.impl.nongwt.attribute.file;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

/** Attribute utility. Provides an instance of mock-up {@link FileHandle} and an empty files {@link Array} to allow for
 * easy action look-up of methods consuming these classes.
 *
 * @author MJ */
class MockUpFileHandle extends FileHandle {
    public static final FileHandle INSTANCE = new MockUpFileHandle();
    public static final Array<FileHandle> EMPTY_ARRAY = new Array<FileHandle>(0);

    private MockUpFileHandle() {
    }
}