package ut.com.atlassian.maven.plugins.jgitflow.manager;

/*-
 * #%L
 * JGitFlow :: Maven Plugin
 * %%
 * Copyright (C) 2017 Atlassian Pty, LTD, Ultreia.io
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.io.File;
import java.util.List;
import java.util.Properties;

import com.atlassian.jgitflow.core.InitContext;
import com.atlassian.jgitflow.core.JGitFlow;
import com.atlassian.jgitflow.core.JGitFlowInitCommand;
import com.atlassian.jgitflow.core.exception.DirtyWorkingTreeException;
import com.atlassian.jgitflow.core.util.GitHelper;
import com.atlassian.maven.plugins.jgitflow.ReleaseContext;
import com.atlassian.maven.plugins.jgitflow.exception.MavenJGitFlowException;
import com.atlassian.maven.plugins.jgitflow.exception.UnresolvedSnapshotsException;
import com.atlassian.maven.plugins.jgitflow.helper.JGitFlowSetupHelper;
import com.atlassian.maven.plugins.jgitflow.helper.ProjectHelper;
import com.atlassian.maven.plugins.jgitflow.manager.FlowReleaseManager;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;
import org.apache.maven.settings.Settings;
import org.codehaus.plexus.util.FileUtils;
import org.eclipse.jgit.api.Git;
import org.junit.Test;

import ut.com.atlassian.maven.plugins.jgitflow.testutils.RepoUtil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @since version
 */
public class ReleaseManagerStartReleaseTest extends AbstractFlowManagerTest
{
    @Test(expected = MavenJGitFlowException.class)
    public void uncommittedChangesFails() throws Exception
    {
        String projectSubdir = "basic-pom";
        List<MavenProject> projects = createReactorProjects("rewrite-for-release", projectSubdir);
        File projectRoot = projects.get(0).getBasedir();

        FlowReleaseManager relman = getReleaseManager();

        ReleaseContext ctx = new ReleaseContext(projectRoot);
        ctx.setDefaultReleaseVersion("1.0");
        ctx.setInteractive(false).setNoTag(true);

        try
        {
            MavenSession session = new MavenSession(getContainer(), new Settings(), localRepository, null, null, null, projectRoot.getAbsolutePath(), new Properties(), new Properties(), null);

            relman.start(ctx, projects, session);
        }
        catch (MavenJGitFlowException e)
        {
            assertEquals(DirtyWorkingTreeException.class, e.getCause().getClass());
            throw e;
        }
    }

    @Test(expected = MavenJGitFlowException.class)
    public void existingSameReleaseIsThrown() throws Exception
    {
        String projectSubdir = "basic-pom";
        List<MavenProject> projects = createReactorProjects("rewrite-for-release", projectSubdir);
        File projectRoot = projects.get(0).getBasedir();

        JGitFlow flow = JGitFlow.getOrInit(projectRoot);
        flow.git().checkout().setName(flow.getDevelopBranchName()).call();

        assertOnDevelop(flow);

        initialCommitAll(flow);

        flow.git().checkout().setCreateBranch(true).setName(flow.getReleaseBranchPrefix() + "1.0").call();

        //go back to develop
        flow.git().checkout().setName(flow.getDevelopBranchName()).call();

        assertOnDevelop(flow);

        assertTrue(GitHelper.localBranchExists(flow.git(), flow.getReleaseBranchPrefix() + "1.0"));

        FlowReleaseManager relman = getReleaseManager();

        ReleaseContext ctx = new ReleaseContext(projectRoot);
        ctx.setInteractive(false).setNoTag(true);

        MavenSession session = new MavenSession(getContainer(), new Settings(), localRepository, null, null, null, projectRoot.getAbsolutePath(), new Properties(), new Properties(), null);

        relman.start(ctx, projects, session);

        assertOnRelease(flow, ctx.getDefaultReleaseVersion());

        compareSnapPomFiles(projects);
    }

    @Test(expected = MavenJGitFlowException.class)
    public void existingDifferentReleaseThrows() throws Exception
    {
        String projectSubdir = "basic-pom";
        List<MavenProject> projects = createReactorProjects("rewrite-for-release", projectSubdir);
        File projectRoot = projects.get(0).getBasedir();

        JGitFlow flow = JGitFlow.getOrInit(projectRoot);
        flow.git().checkout().setName(flow.getDevelopBranchName()).call();

        assertOnDevelop(flow);

        initialCommitAll(flow);

        flow.git().checkout().setCreateBranch(true).setName(flow.getReleaseBranchPrefix() + "0.2").call();

        //go back to develop
        flow.git().checkout().setName(flow.getDevelopBranchName()).call();

        assertOnDevelop(flow);

        assertTrue(GitHelper.localBranchExists(flow.git(), flow.getReleaseBranchPrefix() + "0.2"));

        FlowReleaseManager relman = getReleaseManager();

        ReleaseContext ctx = new ReleaseContext(projectRoot);
        ctx.setInteractive(false).setNoTag(true);

        MavenSession session = new MavenSession(getContainer(), new Settings(), localRepository, null, null, null, projectRoot.getAbsolutePath(), new Properties(), new Properties(), null);

        relman.start(ctx, projects, session);

        assertOnRelease(flow, ctx.getDefaultReleaseVersion());

        compareSnapPomFiles(projects);
    }

    @Test
    public void releaseBasicPom() throws Exception
    {
        basicReleaseRewriteTest("basic-pom", "1.0");
    }

    @Test
    public void releaseWithNamespace() throws Exception
    {
        basicReleaseRewriteTest("basic-pom-namespace");
    }

    @Test
    public void releaseWithTagBase() throws Exception
    {
        basicReleaseRewriteTest("basic-pom-with-tag-base");
    }

    @Test(expected = MavenJGitFlowException.class)
    public void releaseWithInternalDifferingSnapshotDeps() throws Exception
    {
        try
        {
            basicReleaseRewriteTest("internal-differing-snapshot-dependencies");
        }
        catch (MavenJGitFlowException e)
        {
            assertEquals(UnresolvedSnapshotsException.class, e.getCause().getClass());
            throw e;
        }
    }

    @Test
    public void releaseWithInternalDifferingSnapshotDepsAllow() throws Exception
    {
        String projectName = "internal-differing-snapshot-dependencies";
        List<MavenProject> projects = createReactorProjects("rewrite-for-release", projectName);
        File projectRoot = projects.get(0).getBasedir();

        ReleaseContext ctx = new ReleaseContext(projectRoot);

        ctx.setInteractive(false).setNoTag(true).setAllowSnapshots(true);

        basicReleaseRewriteTest(projectName, ctx);
    }

    @Test(expected = MavenJGitFlowException.class)
    public void releaseWithInternalDifferingSnapshotExtension() throws Exception
    {
        try
        {
            basicReleaseRewriteTest("internal-differing-snapshot-extension");
        }
        catch (MavenJGitFlowException e)
        {
            assertEquals(UnresolvedSnapshotsException.class, e.getCause().getClass());
            throw e;
        }
    }

    @Test
    public void releaseWithInternalDifferingSnapshotExtensionAllow() throws Exception
    {
        String projectName = "internal-differing-snapshot-extension";
        List<MavenProject> projects = createReactorProjects("rewrite-for-release", projectName);
        File projectRoot = projects.get(0).getBasedir();

        ReleaseContext ctx = new ReleaseContext(projectRoot);

        ctx.setInteractive(false).setNoTag(true).setAllowSnapshots(true);

        basicReleaseRewriteTest(projectName, ctx);
    }

    @Test(expected = MavenJGitFlowException.class)
    public void releaseWithInternalDifferingSnapshotPlugins() throws Exception
    {
        try
        {
            basicReleaseRewriteTest("internal-differing-snapshot-plugins");
        }
        catch (MavenJGitFlowException e)
        {
            assertEquals(UnresolvedSnapshotsException.class, e.getCause().getClass());
            throw e;
        }
    }

    @Test
    public void releaseWithInternalDifferingSnapshotPluginsAllow() throws Exception
    {
        String projectName = "internal-differing-snapshot-plugins";
        List<MavenProject> projects = createReactorProjects("rewrite-for-release", projectName);
        File projectRoot = projects.get(0).getBasedir();

        ReleaseContext ctx = new ReleaseContext(projectRoot);

        ctx.setInteractive(false).setNoTag(true).setAllowSnapshots(true);

        basicReleaseRewriteTest(projectName, ctx);
    }

    @Test(expected = MavenJGitFlowException.class)
    public void releaseWithInternalDifferingSnapshotReportPlugins() throws Exception
    {
        try
        {
            basicReleaseRewriteTest("internal-differing-snapshot-report-plugins");
        }
        catch (MavenJGitFlowException e)
        {
            assertEquals(UnresolvedSnapshotsException.class, e.getCause().getClass());
            throw e;
        }
    }

    @Test
    public void releaseWithInternalDifferingSnapshotReportPluginsAllow() throws Exception
    {
        String projectName = "internal-differing-snapshot-report-plugins";
        List<MavenProject> projects = createReactorProjects("rewrite-for-release", projectName);
        File projectRoot = projects.get(0).getBasedir();

        ReleaseContext ctx = new ReleaseContext(projectRoot);

        ctx.setInteractive(false).setNoTag(true).setAllowSnapshots(true);

        basicReleaseRewriteTest(projectName, ctx);
    }

    @Test
    public void releaseWithInternalManagedSnapshotDeps() throws Exception
    {
        basicReleaseRewriteTest("internal-managed-snapshot-dependency");
    }

    @Test
    public void releaseWithInternalManagedSnapshotPlugin() throws Exception
    {
        basicReleaseRewriteTest("internal-managed-snapshot-plugin");
    }

    @Test
    public void releaseWithInternalSnapshotDeps() throws Exception
    {
        basicReleaseRewriteTest("internal-snapshot-dependencies");
    }

    @Test
    public void releaseWithInternalSnapshotExtension() throws Exception
    {
        basicReleaseRewriteTest("internal-snapshot-extension");
    }

    @Test
    public void releaseWithInternalSnapshotPluginDeps() throws Exception
    {
        basicReleaseRewriteTest("internal-snapshot-plugin-deps");
    }

    @Test
    public void releaseWithInternalSnapshotPlugins() throws Exception
    {
        basicReleaseRewriteTest("internal-snapshot-plugins");
    }

    @Test
    public void releaseWithInternalSnapshotProfile() throws Exception
    {
        basicReleaseRewriteTest("internal-snapshot-profile");
    }

    @Test
    public void releaseWithInternalSnapshotReportPlugins() throws Exception
    {
        basicReleaseRewriteTest("internal-snapshot-report-plugins");
    }

    @Test
    public void releaseWithInterpolatedVersions() throws Exception
    {
        basicReleaseRewriteTest("interpolated-versions");
    }

    @Test
    public void releaseWithDeepMultimodule() throws Exception
    {
        basicReleaseRewriteTest("multimodule-with-deep-subprojects");
    }

    @Test
    public void releaseWithMultimoduleAlternatePom() throws Exception
    {
        basicReleaseRewriteTest("multimodule-with-alternate-pom");
    }

    @Test
    public void releaseWithInheritedVersion() throws Exception
    {
        basicReleaseRewriteTest("pom-with-inherited-version");
    }

    @Test
    public void releaseWithParent() throws Exception
    {
        basicReleaseRewriteTest("pom-with-parent");
    }

    @Test
    public void releaseWithParentAndProperties() throws Exception
    {
        basicReleaseRewriteTest("pom-with-parent-and-properties");
    }

    @Test
    public void releaseBasicPomWithCustomBranchPrefix() throws Exception
    {
        String projectName = "basic-pom";
        List<MavenProject> projects = createReactorProjects("rewrite-for-release", projectName);
        File projectRoot = projects.get(0).getBasedir();

        InitContext init = new InitContext();
        init.setRelease("superguy/");

        ReleaseContext ctx = new ReleaseContext(projectRoot);

        ctx.setInteractive(false)
           .setNoTag(true)
           .setAllowSnapshots(true)
           .setFlowInitContext(init)
           .setDefaultReleaseVersion("1.0");

        basicReleaseRewriteTest(projectName, ctx);
    }

    @Test
    public void releaseWithFlatParent() throws Exception
    {
        List<MavenProject> projects = createReactorProjects("rewrite-for-release/pom-with-parent-flat", "root-project");
        File projectRoot = projects.get(0).getBasedir().getParentFile();

        JGitFlow flow = JGitFlow.getOrInit(projectRoot);
        flow.git().checkout().setName(flow.getDevelopBranchName()).call();

        assertOnDevelop(flow);

        initialCommitAll(flow);
        FlowReleaseManager relman = getReleaseManager();

        ReleaseContext ctx = new ReleaseContext(projectRoot);
        ctx.setInteractive(false).setNoTag(true);

        MavenSession session = new MavenSession(getContainer(), new Settings(), localRepository, null, null, null, projectRoot.getAbsolutePath(), new Properties(), new Properties(), null);

        relman.start(ctx, projects, session);

        assertOnRelease(flow, "1.0");

        compareSnapPomFiles(projects);
    }

    @Test
    public void releaseWithCustomDevVersion() throws Exception
    {
        String projectName = "basic-pom-custom-dev-version";
        List<MavenProject> projects = createReactorProjects("rewrite-for-release", projectName);
        File projectRoot = projects.get(0).getBasedir();

        ReleaseContext ctx = new ReleaseContext(projectRoot);

        ctx.setInteractive(false)
           .setNoTag(true)
           .setAllowSnapshots(true)
           .setDefaultDevelopmentVersion("2.0-SNAPSHOT");

        basicReleaseRewriteTest(projectName, ctx);
    }

    @Test
    public void releaseWith3DigitVersionIncrementDefault() throws Exception
    {
        String projectName = "3-digit-version-increment-default";
        List<MavenProject> projects = createReactorProjects("rewrite-for-release", projectName);
        File projectRoot = projects.get(0).getBasedir();

        ReleaseContext ctx = new ReleaseContext(projectRoot);

        ctx.setInteractive(false)
           .setNoTag(true)
           .setAllowSnapshots(true);

        basicReleaseRewriteTest(projectName, ctx);
    }

    @Test
    public void releaseWith3DigitVersionIncrementMajor() throws Exception
    {
        String projectName = "3-digit-version-increment-major";
        List<MavenProject> projects = createReactorProjects("rewrite-for-release", projectName);
        File projectRoot = projects.get(0).getBasedir();

        ReleaseContext ctx = new ReleaseContext(projectRoot);

        ctx.setInteractive(false)
           .setNoTag(true)
           .setAllowSnapshots(true)
           .setVersionNumberToIncrement("0");

        basicReleaseRewriteTest(projectName, ctx);
    }

    @Test
    public void releaseWith3DigitVersionIncrementMinor() throws Exception
    {
        String projectName = "3-digit-version-increment-minor";
        List<MavenProject> projects = createReactorProjects("rewrite-for-release", projectName);
        File projectRoot = projects.get(0).getBasedir();

        ReleaseContext ctx = new ReleaseContext(projectRoot);

        ctx.setInteractive(false)
           .setNoTag(true)
           .setAllowSnapshots(true)
           .setVersionNumberToIncrement("1");

        basicReleaseRewriteTest(projectName, ctx);
    }

    @Test
    public void releaseWithComplexVersionIncrementMajor() throws Exception
    {
        String projectName = "complex-version-and-suffix-increment-major";
        List<MavenProject> projects = createReactorProjects("rewrite-for-release", projectName);
        File projectRoot = projects.get(0).getBasedir();

        ReleaseContext ctx = new ReleaseContext(projectRoot);

        ctx.setInteractive(false)
           .setNoTag(true)
           .setAllowSnapshots(true)
           .setVersionNumberToIncrement("0");

        basicReleaseRewriteTest(projectName, ctx);
    }

    @Test
    public void releaseWithComplexVersionIncrementMinor() throws Exception
    {
        String projectName = "complex-version-and-suffix-increment-minor";
        List<MavenProject> projects = createReactorProjects("rewrite-for-release", projectName);
        File projectRoot = projects.get(0).getBasedir();

        ReleaseContext ctx = new ReleaseContext(projectRoot);

        ctx.setInteractive(false)
           .setNoTag(true)
           .setAllowSnapshots(true)
           .setVersionNumberToIncrement("1");

        basicReleaseRewriteTest(projectName, ctx);
    }

    @Test
    public void releaseWithComplexVersionSuffixNoSnap() throws Exception
    {
        String projectName = "complex-version-and-suffix-nosnap";
        List<MavenProject> projects = createReactorProjects("rewrite-for-release", projectName);
        File projectRoot = projects.get(0).getBasedir();

        ReleaseContext ctx = new ReleaseContext(projectRoot);

        ctx.setInteractive(false)
                .setNoTag(true)
                .setAllowSnapshots(true)
                .setReleaseBranchVersionSuffix("RC")
                .setReleaseSnapshots(false);

        basicReleaseRewriteTest(projectName, ctx);
    }

    @Test
    public void releaseWithReleasedParent() throws Exception
    {
        basicReleaseRewriteTest("pom-with-released-parent");
    }

    @Test
    public void startReleaseWithMasterOnly() throws Exception
    {
        ProjectHelper projectHelper = (ProjectHelper) lookup(ProjectHelper.class.getName());
        JGitFlowSetupHelper setupHelper = (JGitFlowSetupHelper) lookup(JGitFlowSetupHelper.class.getName());

        Git git = null;
        Git remoteGit = null;

        List<MavenProject> remoteProjects = createReactorProjects("remote-git-project", null);

        File remoteDir = remoteProjects.get(0).getBasedir();

        //make sure we're clean
        File remoteGitDir = new File(remoteDir, ".git");
        if (remoteGitDir.exists())
        {
            FileUtils.cleanDirectory(remoteGitDir);
        }

        remoteGit = RepoUtil.createRepositoryWithMaster(remoteDir);
        projectHelper.commitAllChanges(remoteGit, "remote commit");

        File localProject = new File(testFileBase, "projects/local/local-git-project");
        git = Git.cloneRepository().setDirectory(localProject).setURI("file://" + remoteGit.getRepository().getWorkTree().getPath()).call();

        List<MavenProject> projects = createReactorProjects("remote-git-project", "local/local-git-project", null, false);
        File projectRoot = projects.get(0).getBasedir();

        JGitFlowInitCommand initCommand = new JGitFlowInitCommand();
        JGitFlow flow = initCommand.setDirectory(git.getRepository().getWorkTree()).call();

        ReleaseContext ctx = new ReleaseContext(projectRoot);

        flow.releaseStart("1.0").call();

        assertEquals(flow.getReleaseBranchPrefix() + "1.0", git.getRepository().getBranch());


    }

    @Test
    public void releaseWithAddingFixLevelVersion() throws Exception
    {
        String projectName = "basic-pom-add-fixlevel-version";
        List<MavenProject> projects = createReactorProjects("rewrite-for-release", projectName);
        File projectRoot = projects.get(0).getBasedir();

        ReleaseContext ctx = new ReleaseContext(projectRoot);

        ctx.setInteractive(false).setNoTag(true).setReleaseBranchVersionSuffix("").setAddFixLevel(true);

        basicReleaseRewriteTest(projectName, ctx);
    }

}
