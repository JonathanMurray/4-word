package old;

import android.graphics.Color;
import android.graphics.Typeface;
import com.example.android_test.GridScene;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.physics.PhysicsHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.util.FPSLogger;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.SimpleBaseGameActivity;

/**
 * Created by jonathan on 2015-06-16.
 */
public class MyGameActivity extends SimpleBaseGameActivity {

    private int CAM_WIDTH = 150;
    private int CAM_HEIGHT = 100;
    private Camera camera;
    private Font font;

    @Override
    protected void onCreateResources() {
        font = FontFactory.create(this.getFontManager(), this.getTextureManager(), 1024, 1024, Typeface.create(Typeface.DEFAULT, Typeface.NORMAL), 16, Color.WHITE);
        font.load();
    }

    @Override
    protected Scene onCreateScene() {
        this.mEngine.registerUpdateHandler(new FPSLogger());

        final GridScene gridScene = new GridScene(this, font, 20, 4, 4, this.getVertexBufferObjectManager(), camera);
       /* gridScene.setCharAtCell('A', 0, 0);
        gridScene.setCharAtCell('X', 1, 1);
        gridScene.setCharAtCell('B', 1, 3);

        gridScene.setGridTouchListener(new GridTouchListener() {
            @Override
            public void gridTouchEvent(int xCell, int yCell) {
                if(gridScene.getCharAtCell(xCell, yCell) == 'X'){
                    gridScene.setCharAtCell('Y', xCell, yCell);
                }else{
                    gridScene.setCharAtCell('X', xCell, yCell);
                }
            }
        });*/
        return gridScene;
    }

    @Override
    public EngineOptions onCreateEngineOptions() {
        camera = new Camera(0, 0, 200, 100);
        return new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new RatioResolutionPolicy(camera.getWidth(), camera.getHeight()), camera);
    }

    private class Obj extends Rectangle{

        private PhysicsHandler physics;
        private float velocity = 10;
        private int direction = -1;

        public Obj(float pX, float pY, float pWidth, float pHeight, VertexBufferObjectManager pVertexBufferObjectManager) {
            super(pX, pY, pWidth, pHeight, pVertexBufferObjectManager);
            physics = new PhysicsHandler(this);
            registerUpdateHandler(physics);
            physics.setVelocity(direction * velocity, 0);

        }

        void incrementVelocity(int vel){
            velocity += vel;
            physics.setVelocity(direction * velocity, 0);
        }


        @Override
        protected void onManagedUpdate(final float seconds){
            if(mX < 0){
                direction = 1;
            }
            if(mX > 200){
                direction = -1;
            }
            physics.setVelocity(direction * velocity, 0);
            super.onManagedUpdate(seconds);
        }
    }

}
