{{- define "fullname" -}}
{{ .Values.name }}
{{- end -}}

{{- define "labels" -}}
layer: {{ .Values.label.layer }}
app: {{ .Values.label.app }}
chartname: {{ .Chart.Name }}
{{- end -}}
