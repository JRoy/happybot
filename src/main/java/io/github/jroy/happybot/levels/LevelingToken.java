package io.github.jroy.happybot.levels;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.core.entities.Member;

@RequiredArgsConstructor
@Getter
public class LevelingToken {
  private final Member member;
  private final long exp;
  private final long level;
}