package se.liu.alfsj019.entity;


/**
 * Represents the state of a movable object on the map to be able
 * to determine the correct animation for the object.
 */
public enum ObjectState
{
    IDLE, WALKING, RUNNING, JUMPING, MELEEING, HURTING, DYING, FALLING, WALK_MELEEING, RUN_MELEEING, ROLLING, CROUCHING
}
