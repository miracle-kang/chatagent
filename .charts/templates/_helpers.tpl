{{/*
Expand the name of the chart.
*/}}
{{- define "helm.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Create a default fully qualified app name.
We truncate at 63 chars because some Kubernetes name fields are limited to this (by the DNS naming spec).
If release name contains chart name it will be used as a full name.
*/}}
{{- define "helm.fullname" -}}
{{- if .Values.fullnameOverride }}
{{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- $name := default .Chart.Name .Values.nameOverride }}
{{- if contains $name .Release.Name }}
{{- .Release.Name | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- printf "%s-%s" .Release.Name $name | trunc 63 | trimSuffix "-" }}
{{- end }}
{{- end }}
{{- end }}

{{/*
Create chart name and version as used by the chart label.
*/}}
{{- define "helm.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Common labels
*/}}
{{- define "helm.labels" -}}
helm.sh/chart: {{ include "helm.chart" . }}
{{ include "helm.selectorLabels" . }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end }}

{{/*
Selector labels
*/}}
{{- define "helm.selectorLabels" -}}
app.kubernetes.io/name: {{ include "helm.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end }}

{{/*
Create the name of the service account to use
*/}}
{{- define "helm.serviceAccountName" -}}
{{- if .Values.serviceAccount.create }}
{{- default (include "helm.fullname" .) .Values.serviceAccount.name }}
{{- else }}
{{- default "default" .Values.serviceAccount.name }}
{{- end }}
{{- end }}

{{/*
Create the name of the config server
*/}}
{{- define "helm.configserver.name" -}}
{{- .Values.configserver.nameOverride | default (printf "%s-%s" (include "helm.name" .) "configserver") }}
{{- end }}

{{- define "helm.configserver.fullname" -}}
{{- .Values.configserver.fullnameOverride | default (printf "%s-%s" (include "helm.fullname" .) "configserver") }}
{{- end }}

{{- define "helm.configserver.labels" -}}
helm.sh/chart: {{ include "helm.chart" . }}
{{ include "helm.configserver.selectorLabels" . }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end }}

{{- define "helm.configserver.selectorLabels" -}}
app.kubernetes.io/name: {{ include "helm.configserver.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end }}

{{- define "helm.configserver.serviceAccountName" -}}
{{- if (default .Values.serviceAccount.create .Values.configserver.serviceAccount.create) }}
{{- default (include "helm.configserver.fullname" .) .Values.configserver.serviceAccount.name }}
{{- else }}
{{- include "helm.serviceAccountName" . }}
{{- end }}
{{- end }}

{{/*
Create the name of the identity servcie
*/}}
{{- define "helm.identity.name" -}}
{{- .Values.identity.nameOverride | default (printf "%s-%s" (include "helm.name" .) "identity") }}
{{- end }}

{{- define "helm.identity.fullname" -}}
{{- .Values.identity.fullnameOverride | default (printf "%s-%s" (include "helm.fullname" .) "identity") }}
{{- end }}

{{- define "helm.identity.labels" -}}
helm.sh/chart: {{ include "helm.chart" . }}
{{ include "helm.identity.selectorLabels" . }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end }}

{{- define "helm.identity.selectorLabels" -}}
app.kubernetes.io/name: {{ include "helm.identity.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end }}

{{- define "helm.identity.serviceAccountName" -}}
{{- if (default .Values.serviceAccount.create .Values.identity.serviceAccount.create) }}
{{- default (include "helm.identity.fullname" .) .Values.identity.serviceAccount.name }}
{{- else }}
{{- include "helm.serviceAccountName" . }}
{{- end }}
{{- end }}

{{/*
Create the name of the subscription service
*/}}
{{- define "helm.subscription.name" -}}
{{- .Values.subscription.nameOverride | default (printf "%s-%s" (include "helm.name" .) "subscription") }}
{{- end }}

{{- define "helm.subscription.fullname" -}}
{{- .Values.subscription.fullnameOverride | default (printf "%s-%s" (include "helm.fullname" .) "subscription") }}
{{- end }}

{{- define "helm.subscription.labels" -}}
helm.sh/chart: {{ include "helm.chart" . }}
{{ include "helm.subscription.selectorLabels" . }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end }}

{{- define "helm.subscription.selectorLabels" -}}
app.kubernetes.io/name: {{ include "helm.subscription.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end }}

{{- define "helm.subscription.serviceAccountName" -}}
{{- if (default .Values.serviceAccount.create .Values.subscription.serviceAccount.create) }}
{{- default (include "helm.subscription.fullname" .) .Values.subscription.serviceAccount.name }}
{{- else }}
{{- include "helm.serviceAccountName" . }}
{{- end }}
{{- end }}

{{/*
Create the name of the assistant service
*/}}
{{- define "helm.assistant.name" -}}
{{- .Values.assistant.nameOverride | default (printf "%s-%s" (include "helm.name" .) "assistant") }}
{{- end }}

{{- define "helm.assistant.fullname" -}}
{{- .Values.assistant.fullnameOverride | default (printf "%s-%s" (include "helm.fullname" .) "assistant") }}
{{- end }}

{{- define "helm.assistant.labels" -}}
helm.sh/chart: {{ include "helm.chart" . }}
{{ include "helm.assistant.selectorLabels" . }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end }}

{{- define "helm.assistant.selectorLabels" -}}
app.kubernetes.io/name: {{ include "helm.assistant.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end }}

{{- define "helm.assistant.serviceAccountName" -}}
{{- if (default .Values.serviceAccount.create .Values.assistant.serviceAccount.create) }}
{{- default (include "helm.assistant.fullname" .) .Values.assistant.serviceAccount.name }}
{{- else }}
{{- include "helm.serviceAccountName" . }}
{{- end }}
{{- end }}
