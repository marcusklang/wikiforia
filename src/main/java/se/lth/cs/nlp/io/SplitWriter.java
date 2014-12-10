package se.lth.cs.nlp.io;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOError;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Split writer, used in parallel split writer
 */
public abstract class SplitWriter {
    private AtomicLong splitCounter = new AtomicLong(0);
    protected File basepath;

    public SplitWriter(File basepath) {
        this.basepath = basepath;
        if(!this.basepath.exists())
            if(!this.basepath.mkdirs()) {
                throw new IOError(new IOException("Failed to mkdirs " + basepath.getAbsolutePath()));
            }
    }

    protected File newSplit() {
        return new File(basepath, "part-" + StringUtils.leftPad(String.valueOf(splitCounter.addAndGet(1)), 5, '0'));
    }
}
