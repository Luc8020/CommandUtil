package com.util.command.example;

import com.util.command.annotations.Argument;
import com.util.command.annotations.Command;
import com.util.command.annotations.Subcommand;

public class NestedCommands {
    @Argument(name = "-id", subcommands = "video.delete.force,video.delete.soft")
    private String videoId;

    @Argument(name = "-reason", subcommands = "video.delete.force")
    private String reason;

    @Argument(name = "-backup", subcommands = "video.delete.soft")
    private boolean createBackup;

    @Command(name = "video")
    public void video() {
        System.out.println("Video management system");
        System.out.println("Available subcommands: delete, list, edit");
    }

    @Subcommand(name = "delete", parent = "video")
    public void delete() {
        System.out.println("Video deletion options:");
        System.out.println("  force - Force delete immediately");
        System.out.println("  soft - Soft delete with backup");
    }

    @Subcommand(name = "force", parent = "video.delete")
    public void forceDelete() {
        System.out.println("Force deleting video: " + videoId);
        if (reason != null) {
            System.out.println("Reason: " + reason);
        }
    }

    @Subcommand(name = "soft", parent = "video.delete")
    public void softDelete() {
        System.out.println("Soft deleting video: " + videoId);
        if (createBackup) {
            System.out.println("Creating backup before deletion");
        }
    }
}
