package com.example;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

@ApplicationPath("/api")
public class RestApplication extends Application {
    // Cette classe enregistre automatiquement tous les endpoints REST
    // Les endpoints seront accessibles via /api/*
}
