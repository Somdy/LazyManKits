package rs.lazymankits;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.Sys;

import java.time.LocalDate;

public class LMDebug {
    private static Logger logger;
    private static final String MARK_LINE_START = "=L=M=K=>";
    private static final String MARK_LINE_END = "<=K=M=L=";

    public static void Log(Object what) {
        Log(LMDebug.class, what);
    }

    public static void Log(@NotNull Object who, Object what) {
        logger = LogManager.getLogger(who.getClass().getName());
        logger.info(MARK_LINE_START + what + MARK_LINE_END);
    }

    public static void deLog(@NotNull Object who, Object what) {
        logger = LogManager.getLogger(who.getClass().getName());
        logger.info(what);
    }
}