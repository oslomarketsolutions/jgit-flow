package com.atlassian.maven.plugins.jgitflow.helper;

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

import java.util.List;

import com.atlassian.maven.plugins.jgitflow.VersionState;
import com.atlassian.maven.plugins.jgitflow.exception.MavenJGitFlowException;
import com.atlassian.maven.plugins.jgitflow.provider.ProjectCacheKey;

import org.apache.maven.project.MavenProject;
import org.eclipse.jgit.api.Git;

/**
 * @since version
 */
public interface ProjectHelper
{
    public static final String AT_PARENT = "parent";
    public static final String AT_DEPENDENCY = "dependency";
    public static final String AT_DEPENDENCY_MGNT = "dependency management";
    public static final String AT_PLUGIN = "plugin";
    public static final String AT_PLUGIN_MGNT = "plugin management";
    public static final String AT_REPORT = "report";
    public static final String AT_EXTENSIONS = "extensions";

    void commitAllChanges(Git git, String message) throws MavenJGitFlowException;

    void commitAllPoms(Git git, List<MavenProject> reactorProjects, String message, boolean noVerify) throws MavenJGitFlowException;

    void checkPomForVersionState(VersionState state, List<MavenProject> reactorProjects) throws MavenJGitFlowException;

    List<String> checkForNonReactorSnapshots(ProjectCacheKey cacheKey, List<MavenProject> reactorProjects) throws MavenJGitFlowException;

}
