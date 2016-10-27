package net.frozenbit.plugin5zig.cubecraft.commands.bancheck;

import eu.the5zig.util.minecraft.ChatColor;

import java.util.Date;

/**
 * POJO for a punishment given to a user on CubeCraft
 */
public class Punishment {
    private Type type;
    private Date start;
    private String reason;
    private State state;

    public Punishment(Type type, Date start, String reason, State state) {
        this.type = type;
        this.start = start;
        this.reason = reason;
        this.state = state;
    }

    public Type getType() {
        return type;
    }

    public Date getStart() {
        return start;
    }

    public String getReason() {
        return reason;
    }

    public State getState() {
        return state;
    }

    @Override
    public String toString() {
        if (state != State.EXPIRED && state != State.ACTIVE) {
            return String.format("%s/%s %s", type, state, reason);
        } else {
            return String.format("%s %s", type, reason);
        }
    }

    public enum State {
        APPEALED("✔ appealed", ChatColor.DARK_GREEN), APPEAL_DENIED("✕ appealed", ChatColor.DARK_RED),
        EXPIRED("expired", ChatColor.GRAY), APPEAL_WAITING("⧗ appeal waiting", ChatColor.GOLD),
        ACTIVE("active", ChatColor.WHITE);

        public final String text;
        public final ChatColor color;

        State(String text, ChatColor color) {
            this.text = text;
            this.color = color;
        }


        @Override
        public String toString() {
            return color + text + ChatColor.RESET;
        }
    }

    public enum Type {
        MUTED(ChatColor.RED), BANNED(ChatColor.DARK_RED);
        public final ChatColor color;

        Type(ChatColor color) {
            this.color = color;
        }

        @Override
        public String toString() {
            return color + name() + ChatColor.RESET;
        }
    }
}
