package physics;

import org.joml.Vector3f;

public class CollisionBox {
    private Vector3f position; // center position
    private float width, height;
    private float offsetX, offsetY;

    public CollisionBox(Vector3f position, float width, float height, float offsetX, float offsetY) {
        this.position = new Vector3f(position);
        this.width = width;
        this.height = height;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    public Vector3f getPosition() {
        return new Vector3f(position.x + offsetX, position.y, position.z + offsetY);
    }

    public void setPosition(Vector3f position) {
        this.position = new Vector3f(position);
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    // Simple AABB collision check on XZ plane
    public boolean intersects(CollisionBox other) {
        Vector3f thisPos = getPosition();
        Vector3f otherPos = other.getPosition();

        boolean xOverlap = Math.abs(thisPos.x - otherPos.x) < (this.width / 2 + other.width / 2);
        boolean zOverlap = Math.abs(thisPos.z - otherPos.z) < (this.height / 2 + other.height / 2);

        return xOverlap && zOverlap;
    }
}
