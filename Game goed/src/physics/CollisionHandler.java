package physics;

import java.util.List;

public class CollisionHandler {

    public static boolean checkCollision(CollisionBox targetBox, List<CollisionBox> objects) {
        for (CollisionBox objBox : objects) {
            if (targetBox.intersects(objBox)) {
                return true;
            }
        }
        return false;
    }
}
