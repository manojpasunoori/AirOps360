from datetime import UTC, datetime, timedelta
from itertools import count

_counter = count(1000)


def next_reference(prefix: str) -> str:
    return f"{prefix}-{next(_counter)}"


def utc_now() -> datetime:
    return datetime.now(UTC)


def utc_plus(minutes: int) -> datetime:
    return utc_now() + timedelta(minutes=minutes)
