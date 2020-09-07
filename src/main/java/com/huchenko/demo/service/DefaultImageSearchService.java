package com.huchenko.demo.service;

import com.huchenko.demo.domain.Picture;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
public class DefaultImageSearchService implements ImageSearchService {
    private final Map<String, Set<Picture>> pictureCache;

    public DefaultImageSearchService(Map<String, Set<Picture>> pictureCache) {
        this.pictureCache = pictureCache;
    }

    public Optional<Set<Picture>> searchByTerm(String term) {
        Set<Picture> resultSet = new HashSet<>();
        for (String key : pictureCache.keySet()) {
            if (calculate(key, term) < 3) {
                resultSet.addAll(pictureCache.get(key));
            }
        }
        return Optional.of(resultSet);
    }

    static int calculate(String x, String y) {
        int[][] dp = new int[x.length() + 1][y.length() + 1];

        for (int i = 0; i <= x.length(); i++) {
            for (int j = 0; j <= y.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else {
                    dp[i][j] = min(dp[i - 1][j - 1]
                            + costOfSubstitution(x.charAt(i - 1), y.charAt(j - 1)),
                        dp[i - 1][j] + 1,
                        dp[i][j - 1] + 1);
                }
            }
        }

        return dp[x.length()][y.length()];
    }

    public static int costOfSubstitution(char a, char b) {
        return a == b ? 0 : 1;
    }

    public static int min(int... numbers) {
        return Arrays.stream(numbers)
            .min().orElse(Integer.MAX_VALUE);
    }
}
