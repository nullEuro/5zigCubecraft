package net.frozenbit.plugin5zig.cubecraft.gamemodes;


import net.frozenbit.plugin5zig.cubecraft.stalker.Stalker;

public class AssassinationMode extends CubeCraftGameMode {
    private static Stalker stalker;

    @Override
    public String getName() {
        return "Assassination";
    }

    @Override
    public Stalker getStalker() {
        if (stalker == null)
            stalker = new Stalker(this);
        return stalker;
    }
}
