package org.mockito.release.notes.vcs;

import org.mockito.release.exec.ProcessRunner;

/**
 * Vcs services
 */
public class Vcs {

    /**
     * Provides means to get contributions.
     */
    public static ContributionsProvider getContributionsProvider(ProcessRunner runner) {
        return new GitContributionsProvider(new GitLogProvider(runner), new IgnoreCiSkip());
    }

    /**
     * Provides means to get release dates
     */
    public static ReleaseDateProvider getReleaseDateProvider(ProcessRunner runner) {
        return new DefaultReleaseDateProvider(runner);
    }
}