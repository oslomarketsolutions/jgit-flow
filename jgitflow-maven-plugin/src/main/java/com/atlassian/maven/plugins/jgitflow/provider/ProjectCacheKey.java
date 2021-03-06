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

public enum ProjectCacheKey
{
    RELEASE_START_LABEL, MASTER_BRANCH, FEATURE_BRANCH, FEATURE_DEPLOY_LABEL, FEATURE_START_LABEL, FEATURE_FINISH_LABEL, HOTFIX_LABEL, HOTFIX_BRANCH, DEVELOP_BRANCH, RELEASE_BRANCH, RELEASE_FINISH_LABEL, BUILD_NUMBER
}
