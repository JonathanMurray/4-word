package fourword;

import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.scene.Scene;

import java.util.ArrayList;

/**
 * Created by jonathan on 2015-07-16.
 */
public class ExtendedScene extends Scene {


    private final ArrayList<IEntity> attachQueue = new ArrayList<IEntity>();
    private final ArrayList<IEntity> detachQueue = new ArrayList<IEntity>();


    public ExtendedScene(){
        registerUpdateHandler(new IUpdateHandler() {
            @Override
            public void onUpdate(float pSecondsElapsed) { //
                for (IEntity e : detachQueue) {
                    detachChild(e);
                }
                detachQueue.clear();
                for (IEntity e : attachQueue) {
                    attachChild(e);
                }
                attachQueue.clear();
                sortChildren();
            }

            @Override
            public void reset() {

            }
        });
    }

    public void safeDetach(IEntity e){
        if(attachQueue.contains(e)){ //hasn't been attached yet, just cancel it from the queue
            attachQueue.remove(e);
        }else{
            detachQueue.add(e); //has been attached, demand detachment
        }
    }

    public void safeAttach(IEntity e){
        attachQueue.add(e);
    }
}
