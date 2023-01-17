package me.endergaming.plugins.backend.exceptions;

public class AddonRegistrationException extends AddonException {
    public AddonRegistrationException(String err) {
        super("Failed to register addon because: " + err);
    }
}
