const ABSOLUTE_URL_RE = /^[a-z][a-z\d+\-.]*:/i;

export function resolveImageUrl(url?: string | null) {
  const rawUrl = `${url || ""}`.trim();

  if (!rawUrl) {
    return "";
  }

  const normalizedUrl = rawUrl.replace(/\\/g, "/");

  if (ABSOLUTE_URL_RE.test(normalizedUrl)) {
    return normalizedUrl;
  }

  if (normalizedUrl.startsWith("//")) {
    return `${window.location.protocol}${normalizedUrl}`;
  }

  const baseUrl = (
    import.meta.env.VITE_APP_BASE_URL || window.location.origin
  ).replace(/\/$/, "");
  const path = normalizedUrl.replace(/^\/+/, "");

  return `${baseUrl}/${path}`;
}
