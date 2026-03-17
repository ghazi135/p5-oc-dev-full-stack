package com.openclassrooms.mdd_api.auth.validation;

/**
 * Politique de mot de passe : au moins 8 caractères, une minuscule, une majuscule, un chiffre et un caractère spécial.
 */
public final class PasswordPolicy {
    private PasswordPolicy() {}

    /**
     * Vérifie que le mot de passe respecte la politique (longueur et complexité).
     *
     * @param password mot de passe à valider
     * @return true si valide
     */
    public static boolean isValid(String password) {
        if (password == null || password.length() < 8) return false;
        boolean hasLower = password.chars().anyMatch(Character::isLowerCase);
        boolean hasUpper = password.chars().anyMatch(Character::isUpperCase);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        boolean hasSpecial = password.chars().anyMatch(ch -> !Character.isLetterOrDigit(ch));
        return hasLower && hasUpper && hasDigit && hasSpecial;
    }
}
