package com.github.onsdigital.playfair.commands.json;

/**
 * Created by david on 12/03/2015.
 */
public class Credentials {

    public String email;
    public String password;

    /**
     * Optional - only needed when changing a password.
     */
    public String oldPassword;
}
