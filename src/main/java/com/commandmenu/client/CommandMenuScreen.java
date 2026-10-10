package com.commandmenu.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * Menu principal : onglet "Items" (grille + recherche + quantité + cible)
 * et onglet "Commandes" (boutons rapides + champ libre).
 */
public class CommandMenuScreen extends Screen {
    private static final int W = 332;
    private static final int H = 232;

    private static final int COLS = 15;
    private static final int ROWS = 6;
    private static final int CELL = 20;

    private static final int COL_PANEL = 0xEE1B1B24;
    private static final int COL_BORDER = 0xFF6C5CE7;
    private static final int COL_HEADER = 0xFF2D2A4A;
    private static final int COL_HOVER = 0x99FFFFFF;

    private static List<ItemStack> allItems;

    // État conservé pendant que l'écran est ouvert (survit aux rebuildWidgets)
    private int tab = 0; // 0 = items, 1 = commandes
    private int category = 0;
    private int page = 0;
    private String search = "";
    private String quantity = "1";
    private String target = "@s";
    private String customCommand = "";
    private String status = "";
    private int statusColor = 0xFFAAAAAA;

    private List<ItemStack> filtered = new ArrayList<>();
    private EditBox searchBox;

    private int left;
    private int top;

    public CommandMenuScreen() {
        super(Component.translatable("gui.commandmenu.title"));
    }

    private static List<ItemStack> getAllItems() {
        if (allItems == null) {
            allItems = new ArrayList<>();
            BuiltInRegistries.ITEM.stream()
                    .filter(item -> item != Items.AIR)
                    .forEach(item -> allItems.add(new ItemStack(item)));
        }
        return allItems;
    }

    @Override
    protected void init() {
        left = (this.width - W) / 2;
        top = (this.height - H) / 2;

        // Onglets
        addRenderableWidget(Button.builder(Component.translatable("gui.commandmenu.tab.items"), b -> switchTab(0))
                .bounds(left + 8, top + 24, 80, 18).build()).active = tab != 0;
        addRenderableWidget(Button.builder(Component.translatable("gui.commandmenu.tab.commands"), b -> switchTab(1))
                .bounds(left + 92, top + 24, 80, 18).build()).active = tab != 1;

        if (tab == 0) {
            initItemsTab();
        } else {
            initCommandsTab();
        }
    }

    private void switchTab(int newTab) {
        tab = newTab;
        status = "";
        rebuildWidgets();
    }

    // ---------------------------------------------------------------- Items

    private void initItemsTab() {
        searchBox = new EditBox(this.font, left + 8, top + 48, 140, 16, Component.empty());
        searchBox.setMaxLength(50);
        searchBox.setHint(Component.translatable("gui.commandmenu.search"));
        searchBox.setValue(search);
        searchBox.setResponder(text -> {
            search = text;
            page = 0;
            refilter();
        });
        addRenderableWidget(searchBox);

        EditBox qty = new EditBox(this.font, left + 154, top + 48, 44, 16, Component.empty());
        qty.setMaxLength(4);
        qty.setFilter(s -> s.matches("\\d*"));
        qty.setHint(Component.translatable("gui.commandmenu.quantity"));
        qty.setValue(quantity);
        qty.setResponder(text -> quantity = text);
        addRenderableWidget(qty);

        EditBox tgt = new EditBox(this.font, left + 204, top + 48, 120, 16, Component.empty());
        tgt.setMaxLength(60);
        tgt.setHint(Component.translatable("gui.commandmenu.target"));
        tgt.setValue(target);
        tgt.setResponder(text -> target = text);
        addRenderableWidget(tgt);

        addRenderableWidget(Button.builder(Component.literal("<"), b -> changePage(-1))
                .bounds(left + 8, top + 196, 20, 16).build());
        addRenderableWidget(Button.builder(Component.literal(">"), b -> changePage(1))
                .bounds(left + W - 28, top + 196, 20, 16).build());

        refilter();
        setInitialFocus(searchBox);
    }

    private void refilter() {
        String q = search.trim().toLowerCase(Locale.ROOT);
        filtered = new ArrayList<>();
        for (ItemStack stack : getAllItems()) {
            if (q.isEmpty()) {
                filtered.add(stack);
                continue;
            }
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
            if (id.getPath().contains(q.replace(' ', '_'))
                    || stack.getHoverName().getString().toLowerCase(Locale.ROOT).contains(q)) {
                filtered.add(stack);
            }
        }
        page = Math.min(page, maxPage());
    }

    private int perPage() {
        return COLS * ROWS;
    }

    private int maxPage() {
        return Math.max(0, (filtered.size() - 1) / perPage());
    }

    private void changePage(int delta) {
        page = Math.max(0, Math.min(maxPage(), page + delta));
    }

    private int gridX() {
        return left + (W - COLS * CELL) / 2;
    }

    private int gridY() {
        return top + 70;
    }

    /** Index dans {@code filtered} sous la souris, ou -1. */
    private int itemIndexAt(double mx, double my) {
        if (tab != 0) {
            return -1;
        }
        int cx = (int) Math.floor((mx - gridX()) / CELL);
        int cy = (int) Math.floor((my - gridY()) / CELL);
        if (cx < 0 || cx >= COLS || cy < 0 || cy >= ROWS) {
            return -1;
        }
        int index = page * perPage() + cy * COLS + cx;
        return index < filtered.size() ? index : -1;
    }

    private int parseQuantity() {
        try {
            return Math.max(1, Math.min(2304, Integer.parseInt(quantity.trim())));
        } catch (NumberFormatException e) {
            return 1;
        }
    }

    private void give(ItemStack stack, int button) {
        int count;
        if (button == 1) {
            count = 1;
        } else if (hasShiftDown()) {
            count = stack.getMaxStackSize();
        } else {
            count = parseQuantity();
        }
        String who = target.isBlank() ? "@s" : target.trim();
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        runCommand("give " + who + " " + id + " " + count);
    }

    // ------------------------------------------------------------ Commandes

    private void initCommandsTab() {
        List<QuickCommands.Category> cats = QuickCommands.CATEGORIES;
        int catW = (W - 16 - (cats.size() - 1) * 4) / cats.size();
        for (int i = 0; i < cats.size(); i++) {
            final int index = i;
            Button b = Button.builder(Component.literal(cats.get(i).name()), btn -> {
                category = index;
                rebuildWidgets();
            }).bounds(left + 8 + i * (catW + 4), top + 48, catW, 18).build();
            b.active = category != i;
            addRenderableWidget(b);
        }

        List<QuickCommands.Entry> entries = cats.get(category).entries();
        int cols = 3;
        int bw = (W - 16 - (cols - 1) * 4) / cols;
        for (int i = 0; i < entries.size(); i++) {
            QuickCommands.Entry entry = entries.get(i);
            int x = left + 8 + (i % cols) * (bw + 4);
            int y = top + 72 + (i / cols) * 22;
            addRenderableWidget(Button.builder(Component.literal(entry.label()), b -> runCommand(entry.command()))
                    .bounds(x, y, bw, 20)
                    .tooltip(net.minecraft.client.gui.components.Tooltip.create(Component.literal("/" + entry.command())))
                    .build());
        }

        EditBox custom = new EditBox(this.font, left + 8, top + 196, W - 16 - 78, 18, Component.empty());
        custom.setMaxLength(256);
        custom.setHint(Component.translatable("gui.commandmenu.custom"));
        custom.setValue(customCommand);
        custom.setResponder(text -> customCommand = text);
        addRenderableWidget(custom);

        addRenderableWidget(Button.builder(Component.translatable("gui.commandmenu.run"), b -> {
            String cmd = customCommand.trim();
            if (cmd.startsWith("/")) {
                cmd = cmd.substring(1);
            }
            if (!cmd.isEmpty()) {
                runCommand(cmd);
            }
        }).bounds(left + W - 8 - 74, top + 195, 74, 20).build());
    }

    // --------------------------------------------------------------- Common

    private void runCommand(String command) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.player.connection == null) {
            return;
        }
        mc.player.connection.sendCommand(command);
        status = "✔ /" + command;
        statusColor = 0xFF55FF55;
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (super.mouseClicked(mx, my, button)) {
            return true;
        }
        int index = itemIndexAt(mx, my);
        if (index >= 0 && (button == 0 || button == 1)) {
            give(filtered.get(index), button);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double scrollX, double scrollY) {
        if (tab == 0 && scrollY != 0) {
            changePage(scrollY > 0 ? -1 : 1);
            return true;
        }
        return super.mouseScrolled(mx, my, scrollX, scrollY);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void renderBackground(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(g, mouseX, mouseY, partialTick);
        // Cadre
        g.fill(left - 2, top - 2, left + W + 2, top + H + 2, COL_BORDER);
        g.fill(left, top, left + W, top + H, COL_PANEL);
        // En-tête
        g.fill(left, top, left + W, top + 20, COL_HEADER);
        g.drawString(this.font, this.title, left + 8, top + 6, 0xFFFFFFFF, true);
        Component hint = Component.translatable("gui.commandmenu.close_hint");
        g.drawString(this.font, hint, left + W - 8 - this.font.width(hint), top + 6, 0xFF9A9AB5, false);
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        super.render(g, mouseX, mouseY, partialTick);

        ItemStack hovered = null;
        if (tab == 0) {
            int start = page * perPage();
            int hoverIndex = itemIndexAt(mouseX, mouseY);
            for (int i = 0; i < perPage() && start + i < filtered.size(); i++) {
                int x = gridX() + (i % COLS) * CELL;
                int y = gridY() + (i / COLS) * CELL;
                boolean hover = start + i == hoverIndex;
                g.fill(x + 1, y + 1, x + CELL - 1, y + CELL - 1, hover ? COL_HOVER : 0x33FFFFFF);
                g.renderItem(filtered.get(start + i), x + 2, y + 2);
                if (hover) {
                    hovered = filtered.get(start + i);
                }
            }
            if (filtered.isEmpty()) {
                Component none = Component.translatable("gui.commandmenu.no_result");
                g.drawCenteredString(this.font, none, left + W / 2, gridY() + 50, 0xFFAAAAAA);
            }
            Component pageLabel = Component.literal((page + 1) + " / " + (maxPage() + 1) + "  (" + filtered.size() + ")");
            g.drawCenteredString(this.font, pageLabel, left + W / 2, top + 200, 0xFFFFFFFF);
        }

        // Pied de page : dernier résultat ou aide
        Component footer = status.isEmpty()
                ? Component.translatable(tab == 0 ? "gui.commandmenu.help.items" : "gui.commandmenu.help.commands")
                : Component.literal(status);
        int color = status.isEmpty() ? 0xFF8A8AA5 : statusColor;
        g.drawString(this.font, this.font.plainSubstrByWidth(footer.getString(), W - 16), left + 8, top + 219, color, false);

        if (hovered != null) {
            g.renderTooltip(this.font, hovered, mouseX, mouseY);
        }
    }
}
