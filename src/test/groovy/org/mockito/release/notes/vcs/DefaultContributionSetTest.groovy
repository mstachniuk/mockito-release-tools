package org.mockito.release.notes.vcs

import org.mockito.release.notes.format.DefaultFormatter
import org.mockito.release.notes.util.Predicate
import spock.lang.Specification
import spock.lang.Subject

class DefaultContributionSetTest extends Specification {

    @Subject contributions = new DefaultContributionSet({false} as Predicate)

    def "empty contributions"() {
        expect:
        contributions.allCommits.isEmpty()
        contributions.allTickets.isEmpty()
    }

    def "contains referenced tickets"() {
        contributions.add(new GitCommit("a@x", "A", "fixes issue #123"))
        contributions.add(new GitCommit("a@x", "A", "fixes issue 250 and #123"))
        contributions.add(new GitCommit("b@x", "B", """adds new feature
#100
"""))

        expect:
        contributions.allTickets == ["123", "100"] as Set
    }

    def "ignores specific commits"() {
        contributions = new DefaultContributionSet(new IgnoreCiSkip())

        when:
        contributions.add(new GitCommit("a@b", "A", "foo [ci skip] bar"))
        contributions.add(new GitCommit("a@b", "A", "good one"))
        def c = contributions.allCommits as List

        then:
        c.size() == 1
        c[0].message == "good one"
        c[0].authorName == "A"
    }
}
