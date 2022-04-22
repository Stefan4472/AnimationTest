package com.galaxyrun.helper;

import galaxyrun.R;

public enum FontId {
    GALAXY_MONKEY(R.font.galaxy_monkey);

    // R.font ID
    private int rId;

    public int getRId() {
        return rId;
    }

    FontId(int rId) {
        this.rId = rId;
    }
}
