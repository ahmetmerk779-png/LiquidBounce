package com.aether.client.rotation;

/** Yaw/pitch cifti. Minecraft'a bagimli degil, bu yuzden bagimsiz test edilebilir. */
public record Rotation(float yaw, float pitch) {

    public static float wrapDegrees(float degrees) {
        float d = degrees % 360.0f;
        if (d >= 180.0f) {
            d -= 360.0f;
        }
        if (d < -180.0f) {
            d += 360.0f;
        }
        return d;
    }

    /** Verilen fark vektorune bakmak icin gereken rotasyon (Minecraft: yaw 0 = +Z, -90 = +X). */
    public static Rotation lookAt(double dx, double dy, double dz) {
        double horizontal = Math.sqrt(dx * dx + dz * dz);
        float yaw = wrapDegrees((float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0));
        float pitch = (float) -Math.toDegrees(Math.atan2(dy, horizontal));
        return new Rotation(yaw, Math.max(-90.0f, Math.min(90.0f, pitch)));
    }

    /** En kisa yoldan hedefe, her eksende en fazla maxStep derece ilerler. */
    public Rotation stepToward(Rotation target, float maxStep) {
        float dYaw = wrapDegrees(target.yaw - yaw);
        float dPitch = target.pitch - pitch;
        float newYaw = wrapDegrees(yaw + Math.max(-maxStep, Math.min(maxStep, dYaw)));
        float newPitch = pitch + Math.max(-maxStep, Math.min(maxStep, dPitch));
        return new Rotation(newYaw, Math.max(-90.0f, Math.min(90.0f, newPitch)));
    }

    /** Iki rotasyon arasindaki en buyuk eksen farki (derece). */
    public float distanceTo(Rotation other) {
        return Math.max(Math.abs(wrapDegrees(other.yaw - yaw)), Math.abs(other.pitch - pitch));
    }
}
