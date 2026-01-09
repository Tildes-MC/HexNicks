/*
 * This file is part of HexNicks, licensed under the MIT License.
 *
 * Copyright (c) 2020-2022 Majekdor
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package dev.majek.hexnicks.event;

import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent;
import dev.majek.hexnicks.HexNicks;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

/**
 * Handles tab completion for <code>/realname</code> and chat tab completions.
 */
public class PaperTabCompleteEvent implements Listener {

  @EventHandler
  public void onTabComplete(AsyncTabCompleteEvent event) {
    String[] args = event.getBuffer().split(" ");
    String prefix = args.length > 0 ? args[args.length - 1].toLowerCase() : "";

    if (event.isCommand()) {
      if (args[0].contains("realname") && args.length > 1) {
        realnameCompletions(event, prefix);
      }
    } else {
      chatCompletions(event, prefix);
    }
  }

  private void realnameCompletions(@NotNull AsyncTabCompleteEvent event, @NotNull String prefix) {
    List<AsyncTabCompleteEvent.Completion> completions = new ArrayList<>();
    for (Component nickname : HexNicks.core().getNickMap().values()) {
      String textName = PlainTextComponentSerializer.plainText().serialize(nickname);
      if (!textName.toLowerCase().startsWith(prefix)) {
        continue;
      }

      completions.add(AsyncTabCompleteEvent.Completion.completion(textName, nickname));
    }

    event.completions(completions);
  }

  private void chatCompletions(@NotNull AsyncTabCompleteEvent event, @NotNull String prefix) {
    Map<UUID, Component> nickMap = HexNicks.core().getNickMap();

    List<AsyncTabCompleteEvent.Completion> completions = new ArrayList<>();
    for (Player player : Bukkit.getOnlinePlayers()) {
      if (!nickMap.containsKey(player.getUniqueId())) {
        continue;
      }

      Component nickname = nickMap.get(player.getUniqueId());
      String textName = PlainTextComponentSerializer.plainText().serialize(nickname);
      if (!textName.toLowerCase().startsWith(prefix)) {
        continue;
      }

      completions.add(AsyncTabCompleteEvent.Completion.completion(textName, nickname));
    }

    event.completions().addAll(completions);
  }
}
