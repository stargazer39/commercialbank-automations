function humanReadable(time: string | number): string {
  return new Date(time).toLocaleString();
}

export { humanReadable };