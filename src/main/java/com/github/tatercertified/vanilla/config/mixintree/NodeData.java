/**
 * Copyright (c) 2025 QPCrummer
 * This project is Licensed under <a href="https://github.com/Tater-Certified/Potatoptimize/blob/main/LICENSE">MIT</a>
 */
package com.github.tatercertified.vanilla.config.mixintree;

public record NodeData(String source, boolean enabled) {

    public boolean isUser() {
        return "User".equals(this.source);
    }
}
