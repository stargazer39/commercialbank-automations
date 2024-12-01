function urlQueryParamsBuilder(
  baseURL: string,
  params: { [key: string]: any },
  relative?: boolean
) {
  if(relative) {
    baseURL = "http://pp.pp" + baseURL
  }
  // Create a URL object
  const url = new URL(baseURL);

  // Add query parameters to the URL
  Object.keys(params).forEach((key) => {
    const values = params[key];

    if (Array.isArray(values)) {
      for (const p of values) {
        url.searchParams.append(key, p);
      }
      return;
    }

    if (values) url.searchParams.append(key, values);
  });

  if(relative) {
    return url.toString().substring(url.origin.length);
  }
  
  return url.toString();
}

export { urlQueryParamsBuilder };
