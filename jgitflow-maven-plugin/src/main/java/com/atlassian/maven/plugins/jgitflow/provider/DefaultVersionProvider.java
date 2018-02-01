package com.atlassian.maven.plugins.jgitflow.provider;

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

import com.atlassian.jgitflow.core.JGitFlow;
import com.atlassian.maven.plugins.jgitflow.PrettyPrompter;
import com.atlassian.maven.plugins.jgitflow.ReleaseContext;
import com.atlassian.maven.plugins.jgitflow.VersionState;
import com.atlassian.maven.plugins.jgitflow.VersionType;
import com.atlassian.maven.plugins.jgitflow.exception.MavenJGitFlowException;
import com.atlassian.maven.plugins.jgitflow.helper.MavenExecutionHelper;
import com.google.common.collect.ImmutableMap;
import org.apache.maven.artifact.ArtifactUtils;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.release.util.ReleaseUtil;
import org.apache.maven.shared.release.version.JGitFlowVersionInfo;
import org.apache.maven.shared.release.versions.DefaultVersionInfo;
import org.apache.maven.shared.release.versions.VersionParseException;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.components.interactivity.PrompterException;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.util.StringUtils;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

@Component(role = VersionProvider.class)
public class DefaultVersionProvider extends AbstractLogEnabled implements VersionProvider
{
    private final Map<ProjectCacheKey, Map<String, String>> nextReleaseVersions;
    private final Map<ProjectCacheKey, Map<String, String>> nextDevelopmentVersions;
    private final Map<ProjectCacheKey, Map<String, String>> nextHotfixVersions;
    private final Map<ProjectCacheKey, Map<String, String>> lastReleaseVersions;
    private final Map<ProjectCacheKey, Map<String, String>> originalVersions;

    @Requirement
    private PrettyPrompter prompter;

    @Requirement
    private MavenExecutionHelper mavenHelper;

    @Requirement
    private JGitFlowProvider jGitFlowProvider;

    @Requirement
    private ContextProvider contextProvider;

    @Requirement
    private MavenSessionProvider sessionProvider;

    public DefaultVersionProvider()
    {
        this.nextReleaseVersions = new HashMap<ProjectCacheKey, Map<String, String>>();
        this.nextDevelopmentVersions = new HashMap<ProjectCacheKey, Map<String, String>>();
        this.nextHotfixVersions = new HashMap<ProjectCacheKey, Map<String, String>>();
        this.lastReleaseVersions = new HashMap<ProjectCacheKey, Map<String, String>>();
        this.originalVersions = new HashMap<ProjectCacheKey, Map<String, String>>();
    }

    @Override
    public Map<String, String> getOriginalVersions(ProjectCacheKey cacheKey, List<MavenProject> reactorProjects)
    {
        if (!originalVersions.containsKey(cacheKey))
        {
            Map<String, String> versions = new HashMap<String, String>();

            for (MavenProject project : reactorProjects)
            {
                versions.put(ArtifactUtils.versionlessKey(project.getGroupId(), project.getArtifactId()), project.getVersion());
            }

            originalVersions.put(cacheKey, versions);
        }

        return ImmutableMap.copyOf(originalVersions.get(cacheKey));
    }

    @Override
    public Map<String, String> getOriginalVersions(List<MavenProject> reactorProjects)
    {
        Map<String, String> versions = new HashMap<String, String>();

        for (MavenProject project : reactorProjects)
        {
            versions.put(ArtifactUtils.versionlessKey(project.getGroupId(), project.getArtifactId()), project.getVersion());
        }

        return ImmutableMap.copyOf(versions);
    }

    @Override
    public String getRootVersion(ProjectCacheKey cacheKey, List<MavenProject> reactorProjects)
    {
        if (reactorProjects.size() < 1)
        {
            return "";
        }

        MavenProject rootProject = ReleaseUtil.getRootProject(reactorProjects);

        return getOriginalVersions(cacheKey, reactorProjects).get(ArtifactUtils.versionlessKey(rootProject.getGroupId(), rootProject.getArtifactId()));
    }

    @Override
    public String getRootVersion(List<MavenProject> reactorProjects)
    {
        if (reactorProjects.size() < 1)
        {
            return "";
        }

        MavenProject rootProject = ReleaseUtil.getRootProject(reactorProjects);

        return getOriginalVersions(reactorProjects).get(ArtifactUtils.versionlessKey(rootProject.getGroupId(), rootProject.getArtifactId()));
    }

    @Override
    public Map<String, String> getNextVersionsForType(VersionType versionType, ProjectCacheKey cacheKey, List<MavenProject> reactorProjects) throws MavenJGitFlowException
    {
        Map<String, String> versions = new HashMap<String, String>();
        switch (versionType)
        {
            case RELEASE:
                versions = getNextReleaseVersions(cacheKey, reactorProjects);
                break;
            case DEVELOPMENT:
                versions = getNextDevelopmentVersions(cacheKey, reactorProjects);
                break;
            case HOTFIX:
                versions = getNextHotfixVersions(cacheKey, reactorProjects);
                break;
        }

        return versions;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> getNextReleaseVersions(ProjectCacheKey cacheKey, List<MavenProject> reactorProjects) throws MavenJGitFlowException
    {
        ReleaseContext ctx = contextProvider.getContext();
        MavenProject rootProject = ReleaseUtil.getRootProject(reactorProjects);

        return getNextVersions(VersionType.RELEASE, cacheKey, reactorProjects, rootProject, ctx.getDefaultReleaseVersion());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> getNextDevelopmentVersions(ProjectCacheKey cacheKey, List<MavenProject> reactorProjects) throws MavenJGitFlowException
    {
        ReleaseContext ctx = contextProvider.getContext();
        MavenProject rootProject = ReleaseUtil.getRootProject(reactorProjects);

        return getNextVersions(VersionType.DEVELOPMENT, cacheKey, reactorProjects, rootProject, ctx.getDefaultDevelopmentVersion());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> getNextHotfixVersions(ProjectCacheKey cacheKey, List<MavenProject> reactorProjects) throws MavenJGitFlowException
    {
        ReleaseContext ctx = contextProvider.getContext();
        MavenProject rootProject = ReleaseUtil.getRootProject(reactorProjects);

        return getNextVersions(VersionType.HOTFIX, cacheKey, reactorProjects, rootProject, ctx.getDefaultReleaseVersion());
    }

    private Map<String, String> getNextVersions(VersionType versionType, ProjectCacheKey cacheKey, List<MavenProject> reactorProjects, MavenProject rootProject, String contextVersion) throws MavenJGitFlowException
    {
        ReleaseContext ctx = contextProvider.getContext();
        String promptLabel = versionType.name().toLowerCase();
        Map<ProjectCacheKey, Map<String, String>> cache = null;
        VersionState versionState = null;
        boolean doAutoVersion = true;

        switch (versionType)
        {
            case RELEASE:
                cache = nextReleaseVersions;
                doAutoVersion = (ctx.isAutoVersionSubmodules() && ArtifactUtils.isSnapshot(rootProject.getVersion()));
                versionState = VersionState.RELEASE;
                break;

            case DEVELOPMENT:
                cache = nextDevelopmentVersions;
                doAutoVersion = ctx.isAutoVersionSubmodules();
                versionState = VersionState.SNAPSHOT;
                break;

            case HOTFIX:
                cache = nextHotfixVersions;
                doAutoVersion = ctx.isAutoVersionSubmodules();
                versionState = VersionState.RELEASE;
                break;
        }

        checkNotNull(cache);
        checkNotNull(versionState);

        //todo: add getOriginalVersions here to pre-pop
        if (!cache.containsKey(cacheKey))
        {
            Map<String, String> versions = new HashMap<String, String>();

            if (doAutoVersion)
            {
                String rootProjectId = ArtifactUtils.versionlessKey(rootProject.getGroupId(), rootProject.getArtifactId());

                String rootVersion = getNextVersion(versionState, versionType, rootProject, rootProject, contextVersion, promptLabel, ctx.isIncrementDevelopFromReleaseVersion());

                versions.put(rootProjectId, rootVersion);

                for (MavenProject subProject : reactorProjects)
                {
                    String subProjectId = ArtifactUtils.versionlessKey(subProject.getGroupId(), subProject.getArtifactId());
                    versions.put(subProjectId, rootVersion);
                }
            }
            else
            {
                for (MavenProject project : reactorProjects)
                {
                    String projectId = ArtifactUtils.versionlessKey(project.getGroupId(), project.getArtifactId());
                    String moduleVersion = getNextVersion(versionState, versionType, rootProject, project, contextVersion, promptLabel, ctx.isIncrementDevelopFromReleaseVersion());
                    versions.put(projectId, moduleVersion);
                }
            }

            cache.put(cacheKey, versions);
        }

        return ImmutableMap.copyOf(cache.get(cacheKey));
    }

    @Override
    public Map<String, String> getLastReleaseVersions(MavenProject rootProject) throws MavenJGitFlowException
    {
        if (!lastReleaseVersions.containsKey(ProjectCacheKey.MASTER_BRANCH))
        {
            try
            {
                Map<String, String> versions = new HashMap<String, String>();

                JGitFlow flow = jGitFlowProvider.gitFlow();
                MavenSession masterSession = mavenHelper.getSessionForBranch(flow.getMasterBranchName(), rootProject, sessionProvider.getSession());

                List<MavenProject> masterProjects = masterSession.getSortedProjects();

                for (MavenProject project : masterProjects)
                {
                    String projectId = ArtifactUtils.versionlessKey(project.getGroupId(), project.getArtifactId());
                    versions.put(projectId, project.getVersion());
                }

                lastReleaseVersions.put(ProjectCacheKey.MASTER_BRANCH, versions);
            }
            catch (Exception e)
            {
                throw new MavenJGitFlowException("Error getting release versions from master", e);
            }
        }

        return lastReleaseVersions.get(ProjectCacheKey.MASTER_BRANCH);
    }

    protected String getNextVersion(VersionState state, VersionType versionType, MavenProject rootProject, MavenProject project, String contextVersion, String promptLabel, boolean isIncrementDevelopFromReleaseVersion) throws MavenJGitFlowException
    {
        ReleaseContext ctx = contextProvider.getContext();
        String defaultVersion = null;
        String suggestedVersion;

        if (StringUtils.isNotBlank(contextVersion))
        {
            defaultVersion = contextVersion;
        }

        String finalVersion = defaultVersion;

        while (StringUtils.isBlank(finalVersion) || ((VersionState.RELEASE.equals(state) && ArtifactUtils.isSnapshot(finalVersion)) || (VersionState.SNAPSHOT.equals(state) && !ArtifactUtils.isSnapshot(finalVersion))))
        {
            if (VersionType.HOTFIX.equals(versionType))
            {
                String projectId = ArtifactUtils.versionlessKey(project.getGroupId(), project.getArtifactId());
                String lastReleaseVersion = getLastReleaseVersions(rootProject).get(projectId);
                suggestedVersion = getSuggestedHotfixVersion(project, lastReleaseVersion);
            }
            else
            {
                String baseVersion = null;
                // MJF-241: Get next version from the release only if configured
                if (isIncrementDevelopFromReleaseVersion && VersionType.DEVELOPMENT.equals(versionType) && nextReleaseVersions.containsKey(ProjectCacheKey.RELEASE_START_LABEL))
                {
                    // MJF-176: Get next version from the release version
                    Map<String, String> versionCache = nextReleaseVersions.get(ProjectCacheKey.RELEASE_START_LABEL);
                    String projectId = ArtifactUtils.versionlessKey(project.getGroupId(), project.getArtifactId());
                    baseVersion = versionCache.get(projectId);
                }

                if (baseVersion == null)
                {
                    baseVersion = project.getVersion();
                }

                suggestedVersion = getSuggestedVersion(versionType, baseVersion);
            }

            if (ctx.isInteractive())
            {
                String message = MessageFormat.format("What is the " + promptLabel + " version for \"{0}\"? ({1})", project.getName(), ArtifactUtils.versionlessKey(project.getGroupId(), project.getArtifactId()));
                try
                {
                    finalVersion = prompter.promptNotBlank(message, suggestedVersion);
                }
                catch (PrompterException e)
                {
                    throw new MavenJGitFlowException("Error reading version from command line " + e.getMessage(), e);
                }
            }
            else
            {
                finalVersion = suggestedVersion;
            }

        }

        return finalVersion;
    }

    private String getSuggestedVersion(VersionType versionType, String incomingVersion) throws MavenJGitFlowException
    {
        ReleaseContext ctx = contextProvider.getContext();
        String suggestedVersion = "unknown";
        JGitFlowVersionInfo info;
        try
        {
            info = new JGitFlowVersionInfo(incomingVersion);
        }
        catch (VersionParseException e)
        {
            if (ctx.isInteractive())
            {
                try
                {
                    info = new JGitFlowVersionInfo("1.0");
                }
                catch (VersionParseException e1)
                {
                    throw new MavenJGitFlowException("error parsing 1.0 version!!!", e1);
                }
            }
            else
            {
                throw new MavenJGitFlowException("error parsing version: " + e.getMessage(), e);
            }
        }

        if (VersionType.RELEASE.equals(versionType))
        {
            suggestedVersion = info.getReleaseVersionString(ctx.isAddFixLevel());
        }
        else if (VersionType.DEVELOPMENT.equals(versionType))
        {
            suggestedVersion = info.getNextDevelopmentVersion(ctx.getVersionNumberToIncrementAsInt()).getSnapshotVersionString();
        }

        return suggestedVersion;
    }

    private String getSuggestedHotfixVersion(MavenProject rootProject, String lastRelease) throws MavenJGitFlowException
    {
        ReleaseContext ctx = contextProvider.getContext();
        String suggestedVersion = "unknown";
        String defaultVersion = rootProject.getVersion();

        if (StringUtils.isNotBlank(ctx.getDefaultReleaseVersion()))
        {
            defaultVersion = ctx.getDefaultReleaseVersion();
        }

        if (StringUtils.isNotBlank(lastRelease) && !ArtifactUtils.isSnapshot(lastRelease))
        {
            try
            {
                DefaultVersionInfo defaultInfo = new DefaultVersionInfo(defaultVersion);
                DefaultVersionInfo lastReleaseInfo = new DefaultVersionInfo(lastRelease);

                String higherVersion = defaultVersion;

                if (defaultInfo.isSnapshot())
                {
                    higherVersion = lastRelease;
                }
                else if (defaultInfo.compareTo(lastReleaseInfo) < 1)
                {
                    higherVersion = lastRelease;
                }

                final JGitFlowVersionInfo hotfixInfo = new JGitFlowVersionInfo(higherVersion);
                suggestedVersion = hotfixInfo.getHotfixVersionString();
            }
            catch (VersionParseException e)
            {
                //just ignore
            }
        }
        else
        {
            try
            {
                final JGitFlowVersionInfo hotfixInfo = new JGitFlowVersionInfo(defaultVersion);
                suggestedVersion = hotfixInfo.getHotfixVersionString();
            }
            catch (VersionParseException e)
            {
                //ignore
            }
        }

        // Fixup project version, if it is a snapshot, in such a case decrement the snapshot version
        while (StringUtils.isBlank(suggestedVersion) || ArtifactUtils.isSnapshot(suggestedVersion))
        {
            JGitFlowVersionInfo info = null;
            try
            {
                info = new JGitFlowVersionInfo(defaultVersion);
            }
            catch (VersionParseException e)
            {
                if (ctx.isInteractive())
                {
                    try
                    {
                        info = new JGitFlowVersionInfo("2.0");
                    }
                    catch (VersionParseException e1)
                    {
                        throw new MavenJGitFlowException("error parsing 2.0 version!!!", e1);
                    }
                }
                else
                {
                    throw new MavenJGitFlowException("error parsing release version: " + e.getMessage(), e);
                }
            }

            suggestedVersion = info.getDecrementedHotfixVersionString();
        }

        return suggestedVersion;
    }
}
