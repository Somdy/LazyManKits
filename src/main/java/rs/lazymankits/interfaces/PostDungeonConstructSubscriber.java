package rs.lazymankits.interfaces;

import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

@Deprecated
public interface PostDungeonConstructSubscriber extends LMSubscriberInterface {
    /**
     * Call when a dungeon constructor is called
     * @param oneTimeEvents special one time event list passed, nullable
     * @param saveFile save file passed, nullable
     */
    void receivePostDungeonCreated(@Nullable ArrayList<String> oneTimeEvents, @Nullable SaveFile saveFile);
}