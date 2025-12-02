package physics;

import java.util.List;

public class CollisionHandler {

    /**
     * Check if the target collision box collides with any in the provided list.
     * @param targetBox The collision box to check.
     * @param objects List of collision boxes to check against.
     * @return True if a collision is detected.
     */
    public static boolean checkCollision(CollisionBox targetBox, List<CollisionBox> objects) {
        for (CollisionBox objBox : objects) {
            if (targetBox.intersects(objBox)) {
                return true;
            }
        }
        return false;
    }
}
