package org.mockito.release.notes.format;

import org.mockito.release.notes.internal.DateFormat;
import org.mockito.release.notes.model.Contribution;
import org.mockito.release.notes.model.ContributionSet;
import org.mockito.release.notes.model.Improvement;
import org.mockito.release.notes.model.ReleaseNotesData;
import org.mockito.release.util.MultiMap;

import java.util.*;

/**
 * Original formatter
 */
class DefaultFormatter implements SingleReleaseNotesFormatter {

    private final Map<String, String> labelMapping;

    DefaultFormatter(Map<String, String> labelMapping) {
        this.labelMapping = labelMapping;
    }

    String format(Map<String, String> labels, Collection<Improvement> improvements) {
        if (improvements.isEmpty()) {
            return "* No notable improvements. See the commits for detailed changes.";
        }
        StringBuilder sb = new StringBuilder("* Improvements: ").append(improvements.size());
        MultiMap<String, Improvement> byLabel = new MultiMap<String, Improvement>();
        Set<Improvement> remainingImprovements = new LinkedHashSet<Improvement>(improvements);

        //Step 1, find improvements that match input labels
        //Iterate label first because the input labels determine the order
        for (String label : labels.keySet()) {
            for (Improvement i : improvements) {
                if (i.getLabels().contains(label) && remainingImprovements.contains(i)) {
                    remainingImprovements.remove(i);
                    byLabel.put(label, i);
                }
            }
        }

        //Step 2, print out the improvements that match input labels
        for (String label : byLabel.keySet()) {
            String labelCaption = labels.get(label);
            Collection<Improvement> labelImprovements = byLabel.get(label);
            sb.append("\n  * ").append(labelCaption).append(": ").append(labelImprovements.size());
            for (Improvement i : labelImprovements) {
                sb.append("\n    * ").append(CommonFormatting.format(i));
            }
        }

        //Step 3, print out remaining changes
        if (!remainingImprovements.isEmpty()) {
            String indent;
            //We want clean view depending if there are labelled improvements or not
            if (byLabel.size() > 0) {
                indent = "  ";
                sb.append("\n  * Remaining changes: ").append(remainingImprovements.size());
            } else {
                indent = "";
            }

            for (Improvement i : remainingImprovements) {
                sb.append("\n").append(indent).append("  * ").append(CommonFormatting.format(i));
            }
        }
        return sb.toString();
    }

    private String format(Contribution contribution) {
        return contribution.getCommits().size() + ": " + contribution.getAuthorName();
    }

    private String format(ContributionSet contributions) {
        StringBuilder sb = new StringBuilder("* Authors: ").append(contributions.getContributions().size())
                .append("\n* Commits: ").append(contributions.getAllCommits().size());

        for (Contribution c : contributions.getContributions()) {
            sb.append("\n  * ").append(format(c));
        }

        return sb.toString();
    }

    public String formatVersion(ReleaseNotesData data) {
        String now = DateFormat.formatDate(data.getDate());

        return "### " + data.getVersion() + " (" + now + ")" + "\n\n"
                + format(data.getContributions()) + "\n"
                + format(labelMapping, data.getImprovements()) + "\n\n";
    }
}
